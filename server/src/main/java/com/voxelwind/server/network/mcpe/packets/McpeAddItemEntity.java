package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeAddItemEntity implements NetworkPackage {
    private long entityId;
    private long runtimeEntityId;
    private ItemStack stack;
    private Vector3f position;
    private Vector3f velocity;
    private final MetadataDictionary metadata = new MetadataDictionary();

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeUnsigned(buffer, entityId); // runtimeEntityId
        McpeUtil.writeItemStack(buffer, stack);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeVector3f(buffer, velocity);
        metadata.writeTo(buffer);
    }
}
