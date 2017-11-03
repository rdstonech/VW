package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetTime implements NetworkPackage
{
	private int time;

	@Override
	public void decode (ByteBuf buffer)
	{
		time = Varints.decodeSigned (buffer);
	}

	@Override
	public void encode (ByteBuf buffer)
	{
		Varints.encodeSigned (buffer, time);
	}
}
