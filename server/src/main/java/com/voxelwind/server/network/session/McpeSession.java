package com.voxelwind.server.network.session;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.jni.hash.VoxelwindHash;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.PacketRegistry;
import com.voxelwind.server.network.mcpe.annotations.DisallowWrapping;
import com.voxelwind.server.network.mcpe.annotations.ForceClearText;
import com.voxelwind.server.network.mcpe.packets.McpeDisconnect;
import com.voxelwind.server.network.mcpe.packets.McpeWrapper;
import com.voxelwind.server.network.raknet.RakNetSession;
import com.voxelwind.server.network.raknet.handler.NetworkPacketHandler;
import com.voxelwind.server.network.session.auth.ClientData;
import com.voxelwind.server.network.session.auth.UserAuthenticationProfile;
import com.voxelwind.server.network.util.CompressionUtil;
import com.voxelwind.server.network.util.NativeCodeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.extern.log4j.Log4j2;
import net.md_5.bungee.jni.cipher.BungeeCipher;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public class McpeSession
{
	private static final ThreadLocal<VoxelwindHash> hashLocal = new ThreadLocal<VoxelwindHash> ()
	{
		@Override
		protected VoxelwindHash initialValue ()
		{
			return NativeCodeFactory.hash.newInstance ();
		}
	};
	private static final int TIMEOUT_MS = 30000;
	private final AtomicLong encryptedSentPacketGenerator = new AtomicLong ();
	private final Queue<NetworkPackage> currentlyQueued = new ConcurrentLinkedQueue<> ();
	private UserAuthenticationProfile authenticationProfile;
	private ClientData clientData;
	private NetworkPacketHandler handler;
	private BungeeCipher encryptionCipher;
	private BungeeCipher decryptionCipher;
	private PlayerSession playerSession;
	private byte[] serverKey;
	private final SessionConnection connection;
	private final VoxelwindServer server;
	private final AtomicLong lastKnownUpdate = new AtomicLong (System.currentTimeMillis ());

	public McpeSession (NetworkPacketHandler handler, VoxelwindServer server, SessionConnection connection)
	{
		this.server = server;
		this.handler = handler;
		this.connection = connection;
	}

	public UserAuthenticationProfile getAuthenticationProfile ()
	{
		return authenticationProfile;
	}

	public void setAuthenticationProfile (UserAuthenticationProfile authenticationProfile)
	{
		Preconditions.checkNotNull (authenticationProfile, "authenticationProfile");
		this.authenticationProfile = authenticationProfile;
	}

	public NetworkPacketHandler getHandler ()
	{
		return handler;
	}

	public void setHandler (NetworkPacketHandler handler)
	{
		checkForClosed ();
		Preconditions.checkNotNull (handler, "handler");
		this.handler = handler;
	}

	private void checkForClosed ()
	{
		Preconditions.checkState (!connection.isClosed (), "Connection has been closed!");
	}

	public void addToSendQueue (NetworkPackage netPackage)
	{
		checkForClosed ();
		Preconditions.checkNotNull (netPackage, "netPackage");

		// Verify that the packet ID exists.
		PacketRegistry.getId (netPackage);

		currentlyQueued.add (netPackage);
	}

	public void sendImmediatePackage (NetworkPackage netPackage)
	{
		checkForClosed ();
		Preconditions.checkNotNull (netPackage, "netPackage");
		internalSendPackage (netPackage);
	}

	private void internalSendPackage (NetworkPackage netPackage)
	{
		if (log.isDebugEnabled ())
		{
			String to = connection.getRemoteAddress ().map (InetSocketAddress::toString).orElse (connection.toString ());
			log.debug ("Sending packet {} to {}", netPackage, to);
		}

		ByteBuf dataToSend;
		if (!netPackage.getClass ().isAnnotationPresent (DisallowWrapping.class) || netPackage instanceof McpeWrapper)
		{
			dataToSend = PooledByteBufAllocator.DEFAULT.directBuffer ();
			dataToSend.writeByte (0xFE);

			ByteBuf compressed;
			if (netPackage instanceof McpeWrapper)
			{
				McpeWrapper wrapper = (McpeWrapper) netPackage;
				if (wrapper.getPayload () == null)
				{
					compressed = CompressionUtil.compressWrapperPackets (wrapper.getPackets ());
				} else
				{
					compressed = wrapper.getPayload ();
				}
			} else
			{
				compressed = CompressionUtil.compressWrapperPackets (netPackage);
			}

			try
			{
				if (encryptionCipher == null || netPackage.getClass ().isAnnotationPresent (ForceClearText.class))
				{
					compressed.readerIndex (0);
					dataToSend.writeBytes (compressed);
				} else
				{
					compressed.readerIndex (0);
					byte[] trailer = generateTrailer (compressed);
					compressed.writeBytes (trailer);

					compressed.readerIndex (0);
					encryptionCipher.cipher (compressed, dataToSend);
				}
			} catch (GeneralSecurityException e)
			{
				dataToSend.release ();
				throw new RuntimeException ("Unable to encipher package", e);
			} finally
			{
				compressed.release ();
			}
		} else
		{
			dataToSend = PacketRegistry.tryEncode (netPackage);
		}

		connection.sendPacket (dataToSend);
	}

	public void onTick ()
	{
		if (isClosed ())
		{
			return;
		}

		if (isTimedOut ())
		{
			disconnect ("User timed out after " + TIMEOUT_MS + "ms of no new packets being sent.");
			return;
		}

		connection.onTick ();
		sendQueued ();
	}

	private void sendQueued ()
	{
		NetworkPackage netPackage;
		McpeWrapper wrapper = new McpeWrapper ();
		while ((netPackage = currentlyQueued.poll ()) != null)
		{
			if (netPackage.getClass ().isAnnotationPresent (DisallowWrapping.class) ||
					netPackage.getClass ().isAnnotationPresent (ForceClearText.class))
			{
				// We hit a un-batchable packet. Send the current batch and then send the un-batchable packet.
				if (!wrapper.getPackets ().isEmpty ())
				{
					internalSendPackage (wrapper);
					wrapper = new McpeWrapper ();
				}

				internalSendPackage (netPackage);

				try
				{
					// Delay things a tiny bit
					Thread.sleep (1);
				} catch (InterruptedException e)
				{
					log.error ("Interrupted", e);
				}

				continue;
			} else if (wrapper.getPackets ().size () >= 3)
			{
				// Reached a per-batch limit on packages, send these packages now
				internalSendPackage (wrapper);
				wrapper = new McpeWrapper ();

				try
				{
					// Delay things a tiny bit
					Thread.sleep (1);
				} catch (InterruptedException e)
				{
					log.error ("Interrupted", e);
				}
			}

			wrapper.getPackets ().add (netPackage);
		}

		if (!wrapper.getPackets ().isEmpty ())
		{
			internalSendPackage (wrapper);
		}
	}

	void enableEncryption (byte[] secretKey)
	{
		checkForClosed ();

		serverKey = secretKey;
		byte[] iv = Arrays.copyOf (secretKey, 16);
		SecretKey key = new SecretKeySpec (secretKey, "AES");
		try
		{
			encryptionCipher = NativeCodeFactory.cipher.newInstance ();
			decryptionCipher = NativeCodeFactory.cipher.newInstance ();

			encryptionCipher.init (true, key, iv);
			decryptionCipher.init (false, key, iv);
		} catch (GeneralSecurityException e)
		{
			throw new RuntimeException ("Unable to initialize ciphers", e);
		}

		if (connection instanceof RakNetSession)
		{
			((RakNetSession) connection).setUseOrdering (true);
		}
	}

	public boolean isEncrypted ()
	{
		return encryptionCipher != null;
	}

	public void close ()
	{
		connection.close ();

		server.getSessionManager ().remove (this);

		// Free native resources if required
		if (encryptionCipher != null)
		{
			encryptionCipher.free ();
		}
		if (decryptionCipher != null)
		{
			decryptionCipher.free ();
		}

		// Make sure the entity is no longer being ticked
		if (playerSession != null)
		{
			playerSession.removeInternal ();
		}
	}

	public BungeeCipher getEncryptionCipher ()
	{
		return encryptionCipher;
	}

	public BungeeCipher getDecryptionCipher ()
	{
		return decryptionCipher;
	}

	private byte[] generateTrailer (ByteBuf buf)
	{
		VoxelwindHash hash = hashLocal.get ();
		ByteBuf counterBuf = PooledByteBufAllocator.DEFAULT.directBuffer (8);
		ByteBuf keyBuf = PooledByteBufAllocator.DEFAULT.directBuffer (serverKey.length);
		try
		{
			counterBuf.writeLongLE (encryptedSentPacketGenerator.getAndIncrement ());
			keyBuf.writeBytes (serverKey);

			hash.update (counterBuf);
			hash.update (buf);
			hash.update (keyBuf);
			byte[] digested = hash.digest ();
			return Arrays.copyOf (digested, 8);
		} finally
		{
			counterBuf.release ();
			keyBuf.release ();
		}
	}

	public PlayerSession getPlayerSession ()
	{
		return playerSession;
	}

	public PlayerSession initializePlayerSession (VoxelwindLevel level)
	{
		checkForClosed ();
		Preconditions.checkState (playerSession == null, "Player session already initialized");

		playerSession = new PlayerSession (this, level);
		return playerSession;
	}

	public ClientData getClientData ()
	{
		return clientData;
	}

	public void setClientData (ClientData clientData)
	{
		this.clientData = clientData;
	}

	public void disconnect (@Nonnull String reason)
	{
		disconnect (reason, true);
	}

	public void disconnect (@Nonnull String reason, boolean sendReason)
	{
		Preconditions.checkNotNull (reason, "reason");
		checkForClosed ();

		if (sendReason)
		{
			McpeDisconnect packet = new McpeDisconnect ();
			packet.setMessage (reason);
			sendImmediatePackage (packet);
		}

		if (authenticationProfile != null)
		{
			log.info ("{} ({}) has been disconnected from the server: {}", authenticationProfile.getDisplayName (),
					getRemoteAddress ().map (Object::toString).orElse ("UNKNOWN"), reason);
		} else
		{
			log.info ("{} has lost connection to the server: {}", getRemoteAddress ().map (Object::toString).orElse ("UNKNOWN"),
					reason);
		}

		close ();
	}

	public boolean isClosed ()
	{
		return connection.isClosed ();
	}

	public Optional<InetSocketAddress> getRemoteAddress ()
	{
		return connection.getRemoteAddress ();
	}

	private boolean isTimedOut ()
	{
		return System.currentTimeMillis () - lastKnownUpdate.get () >= TIMEOUT_MS;
	}

	public void touch ()
	{
		checkForClosed ();
		lastKnownUpdate.set (System.currentTimeMillis ());
	}

	public SessionConnection getConnection ()
	{
		return connection;
	}

	public VoxelwindServer getServer ()
	{
		return server;
	}
}
