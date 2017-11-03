package com.voxelwind.server.network.raknet.enveloped;

import com.voxelwind.server.network.raknet.datagrams.RakNetDatagram;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

public class AddressedRakNetDatagram extends DefaultAddressedEnvelope<RakNetDatagram, InetSocketAddress> {
    public AddressedRakNetDatagram(RakNetDatagram message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }

    public AddressedRakNetDatagram(RakNetDatagram message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}
