package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetEntityMotion implements NetworkPackage
{
	private long runtimeEntityId;
	private Vector3f motion;

	@Override
	public void decode (ByteBuf buffer)
	{
		runtimeEntityId = Varints.decodeUnsigned (buffer);
		motion = McpeUtil.readVector3f (buffer);
	}

	@Override
	public void encode (ByteBuf buffer)
	{
		Varints.encodeUnsigned (buffer, runtimeEntityId);
		McpeUtil.writeVector3f (buffer, motion);
	}
}
