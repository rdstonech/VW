package com.voxelwind.server.network.rcon;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.util.TextFormat;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.server.command.CommandException;
import com.voxelwind.api.server.command.CommandNotFoundException;
import com.voxelwind.server.network.listeners.RconNetworkListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Log4j2
public class RconHandler extends SimpleChannelInboundHandler<RconMessage>
{
	private final byte[] password;
	private final Server server;
	private final RconNetworkListener listener;
	private boolean authenticated = false;

	public RconHandler (byte[] password, Server server, RconNetworkListener listener)
	{
		this.password = password;
		this.server = server;
		this.listener = listener;
	}

	@Override
	protected void channelRead0 (ChannelHandlerContext ctx, RconMessage message) throws Exception
	{
		if (!authenticated)
		{
			Preconditions.checkArgument (message.getType () == RconMessage.SERVERDATA_AUTH, "Trying to handle unauthenticated RCON message!");
			byte[] providedPassword = message.getBody ().getBytes (StandardCharsets.UTF_8);
			// Send an empty SERVERDATA_RESPONSE_VALUE to emulate SRCDS
			ctx.channel ().writeAndFlush (new RconMessage (message.getId (), RconMessage.SERVERDATA_RESPONSE_VALUE, ""), ctx.voidPromise ());
			// Check the password.
			if (MessageDigest.isEqual (password, providedPassword))
			{
				authenticated = true;
				ctx.channel ().writeAndFlush (new RconMessage (message.getId (), RconMessage.SERVERDATA_AUTH_RESPONSE, ""), ctx.voidPromise ());
			} else
			{
				ctx.channel ().writeAndFlush (new RconMessage (-1, RconMessage.SERVERDATA_AUTH_RESPONSE, ""), ctx.voidPromise ());
			}
		} else
		{
			Preconditions.checkArgument (message.getType () == RconMessage.SERVERDATA_EXECCOMMAND, "Trying to handle non-execute command RCON message for authenticated connection!");
			Channel channel = ctx.channel ();
			listener.getCommandExecutionService ().execute (() ->
			{
				String body;
				try
				{
					RconCommandExecutorSource source = new RconCommandExecutorSource ();
					server.getCommandManager ().executeCommand (source, message.getBody ());
					source.stopOutput ();

					body = TextFormat.removeFormatting (Joiner.on ('\n').join (source.getOutput ()));
				} catch (CommandNotFoundException e)
				{
					body = "No such command found.";
				} catch (CommandException e)
				{
					log.error ("Unable to run command {} for remote connection {}", message.getBody (), channel.remoteAddress (),
							e);
					body = "An error has occurred. Information has been logged to the console.";
				}
				channel.writeAndFlush (new RconMessage (message.getId (), RconMessage.SERVERDATA_RESPONSE_VALUE, body), ctx.voidPromise ());
			});
		}
	}

	@Override
	public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		log.error ("An error occurred whilst handling a RCON request of {}", ctx.channel ().remoteAddress (), cause);
		// Better to close the channel instead.
		ctx.close ();
	}
}
