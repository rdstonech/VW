package com.voxelwind.server.network.raknet.handler;

import com.google.common.net.InetAddresses;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.PacketRegistry;
import com.voxelwind.server.network.PacketType;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.server.network.raknet.RakNetSession;
import com.voxelwind.server.network.raknet.datagrams.EncapsulatedRakNetPacket;
import com.voxelwind.server.network.raknet.datastructs.IntRange;
import com.voxelwind.server.network.raknet.enveloped.AddressedRakNetDatagram;
import com.voxelwind.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import com.voxelwind.server.network.raknet.packets.*;
import com.voxelwind.server.network.session.McpeSession;
import com.voxelwind.server.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Log4j2
public class RakNetDatagramHandler extends SimpleChannelInboundHandler<AddressedRakNetDatagram> {
    private static final InetSocketAddress LOOPBACK_MCPE = new InetSocketAddress(InetAddress.getLoopbackAddress(), 19132);
    private static final InetSocketAddress JUNK_ADDRESS = new InetSocketAddress(InetAddresses.forString("255.255.255.255"), 19132);
    private final VoxelwindServer server;

    public RakNetDatagramHandler(VoxelwindServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedRakNetDatagram datagram) throws Exception {
        McpeSession session = server.getSessionManager().get(datagram.sender());

        if (session == null)
            return;

        // Make sure a RakNet session is backing this packet.
        if (!(session.getConnection() instanceof RakNetSession)) {
            return;
        }

        RakNetSession rakNetSession = (RakNetSession) session.getConnection();

        // Acknowledge receipt of the datagram.
        AckPacket ackPacket = new AckPacket();
        ackPacket.getIds().add(new IntRange(datagram.content().getDatagramSequenceNumber()));
        ctx.writeAndFlush(new DirectAddressedRakNetPacket(ackPacket, datagram.sender()), ctx.voidPromise());

        // Update session touch time.
        session.touch();

        // Check the datagram contents.
        if (datagram.content().getFlags().isValid()) {
            for (EncapsulatedRakNetPacket packet : datagram.content().getPackets()) {
                // Try to figure out what packet got sent.
                if (packet.isHasSplit()) {
                    Optional<ByteBuf> possiblyReassembled = rakNetSession.addSplitPacket(packet);
                    if (possiblyReassembled.isPresent()) {
                        ByteBuf reassembled = possiblyReassembled.get();
                        try {
                            NetworkPackage pkg = PacketRegistry.tryDecode(reassembled, PacketType.RAKNET);
                            handlePackage(pkg, session);
                        } finally {
                            reassembled.release();
                        }
                    }
                } else {
                    // Try to decode the full packet.
                    NetworkPackage pkg = PacketRegistry.tryDecode(packet.getBuffer(), PacketType.RAKNET);
                    handlePackage(pkg, session);
                }
            }
        }
    }

    private void handlePackage(NetworkPackage netPackage, McpeSession session) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Inbound package: {}", netPackage);
        }

        if (netPackage == null) {
            return;
        }

        if (session.getHandler() == null) {
            log.error("Session " + session.getRemoteAddress() + " has no handler!?");
            return;
        }

        // McpeWrapper: Encrypted batch packet.
        if (netPackage instanceof McpeWrapper) {
            List<NetworkPackage> packages;

            ByteBuf wrappedData = ((McpeWrapper) netPackage).getPayload();
            ByteBuf cleartext = null;
            try {
                if (session.isEncrypted()) {
                    cleartext = PooledByteBufAllocator.DEFAULT.directBuffer(wrappedData.readableBytes());
                    session.getDecryptionCipher().cipher(wrappedData, cleartext);
                    // MCPE appends an 8-byte checksum at the end of each packet, but we don't want it.
                    // TODO: Would it be worth checking it?
                    cleartext = cleartext.slice(0, cleartext.readableBytes() - 8);
                } else {
                    cleartext = wrappedData;
                }

                /*if (log.isDebugEnabled()) {
                    log.debug("[MCPE WRAPPER HEX]\n{}", ByteBufUtil.prettyHexDump(cleartext));
                }*/

                packages = CompressionUtil.decompressWrapperPackets(cleartext);
            } finally {
                if (cleartext != null && cleartext != wrappedData) {
                    cleartext.release();
                }
            }

            for (NetworkPackage aPackage : packages) {
                handlePackage(aPackage, session);
            }

            return;
        }

        // Connected Ping
        if (netPackage instanceof ConnectedPingPacket) {
            ConnectedPingPacket request = (ConnectedPingPacket) netPackage;
            ConnectedPongPacket response = new ConnectedPongPacket();
            response.setPingTime(request.getPingTime());
            response.setPongTime(System.currentTimeMillis());
            session.sendImmediatePackage(response);
            return;
        }
        // Connection Request
        if (netPackage instanceof ConnectionRequestPacket) {
            ConnectionRequestPacket request = (ConnectionRequestPacket) netPackage;
            ConnectionResponsePacket response = new ConnectionResponsePacket();
            response.setIncomingTimestamp(request.getTimestamp());
            response.setSystemTimestamp(System.currentTimeMillis());
            response.setSystemAddress(session.getRemoteAddress().orElse(LOOPBACK_MCPE));
            InetSocketAddress[] addresses = new InetSocketAddress[20];
            Arrays.fill(addresses, JUNK_ADDRESS);
            addresses[0] = LOOPBACK_MCPE;
            response.setSystemAddresses(addresses);
            response.setSystemIndex((short) 0);
            session.sendImmediatePackage(response);
            return;
        }
        // Disconnection
        if (netPackage instanceof DisconnectNotificationPacket) {
            session.disconnect("User disconnected from server", false);
            return;
        }

        // Unknown
        if (netPackage instanceof McpeUnknown) {
            if (log.isDebugEnabled()) {
                log.debug("Unknown packet received with ID " + Integer.toHexString(((McpeUnknown) netPackage).getId()));
                log.debug("Dump: {}", ByteBufUtil.hexDump(((McpeUnknown) netPackage).getBuf()));
            }
            ((McpeUnknown) netPackage).getBuf().release();
        }

        // Dispatch block...
        if (netPackage instanceof McpeMovePlayer) {
            session.getHandler().handle((McpeMovePlayer) netPackage);
        }
        if (netPackage instanceof McpeAnimate) {
            session.getHandler().handle((McpeAnimate) netPackage);
        }
        if (netPackage instanceof McpeInventoryTransaction) {
            session.getHandler().handle((McpeInventoryTransaction) netPackage);
        }
        if (netPackage instanceof McpeLogin) {
            session.getHandler().handle((McpeLogin) netPackage);
        }
        if (netPackage instanceof McpeSubClientLogin) {
            session.getHandler().handle((McpeSubClientLogin) netPackage);
        }
        if (netPackage instanceof McpeClientToServerHandshake) {
            session.getHandler().handle((McpeClientToServerHandshake) netPackage);
        }
        if (netPackage instanceof McpeRequestChunkRadius) {
            session.getHandler().handle((McpeRequestChunkRadius) netPackage);
        }
        if (netPackage instanceof McpePlayerAction) {
            session.getHandler().handle((McpePlayerAction) netPackage);
        }
        if (netPackage instanceof McpeText) {
            session.getHandler().handle((McpeText) netPackage);
        }
        if (netPackage instanceof McpeContainerClose) {
            session.getHandler().handle((McpeContainerClose) netPackage);
        }
        if (netPackage instanceof McpeInventorySlot) {
            session.getHandler().handle((McpeInventorySlot) netPackage);
        }
        if (netPackage instanceof McpeMobEquipment) {
            session.getHandler().handle((McpeMobEquipment) netPackage);
        }
        if (netPackage instanceof McpeResourcePackClientResponse) {
            session.getHandler().handle((McpeResourcePackClientResponse) netPackage);
        }
        if (netPackage instanceof McpeCommandRequest) {
            session.getHandler().handle((McpeCommandRequest) netPackage);
        }
        if (netPackage instanceof McpeAdventureSettings) {
            session.getHandler().handle((McpeAdventureSettings) netPackage);
        }
    }
}
