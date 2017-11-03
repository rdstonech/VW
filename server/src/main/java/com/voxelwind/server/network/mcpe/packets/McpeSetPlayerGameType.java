package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetPlayerGameType implements NetworkPackage
{
	private int gamemode;

	@Override
	public void decode (ByteBuf buffer)
	{
		gamemode = Varints.decodeSigned (buffer);
	}

	@Override
	public void encode (ByteBuf buffer)
	{
		Varints.encodeSigned (buffer, gamemode);
	}
}
