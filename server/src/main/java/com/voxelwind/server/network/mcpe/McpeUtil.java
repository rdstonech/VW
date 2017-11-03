package com.voxelwind.server.network.mcpe;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.server.Skin;
import com.voxelwind.api.server.player.TranslatedMessage;
import com.voxelwind.api.util.Rotation;
import com.voxelwind.nbt.io.NBTReader;
import com.voxelwind.nbt.io.NBTWriter;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.nbt.tags.Tag;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.game.item.VoxelwindItemStackBuilder;
import com.voxelwind.server.game.item.VoxelwindNBTUtils;
import com.voxelwind.server.game.level.util.EntityAttribute;
import com.voxelwind.server.game.level.util.PlayerAttribute;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import com.voxelwind.server.network.mcpe.util.CommandOriginData;
import com.voxelwind.server.network.mcpe.util.ResourcePackInfo;
import com.voxelwind.server.network.util.LittleEndianByteBufInputStream;
import com.voxelwind.server.network.util.LittleEndianByteBufOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class McpeUtil {
    private McpeUtil() {

    }

    public static void writeVarintLengthString(ByteBuf buffer, String string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        byte[] bytes = string.getBytes(CharsetUtil.UTF_8);
        Varints.encodeUnsigned(buffer, bytes.length);
        buffer.writeBytes(bytes);
    }

    public static String readVarintLengthString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int length = (int) Varints.decodeUnsigned(buffer);
        byte[] readBytes = new byte[length];
        buffer.readBytes(readBytes);
        return new String(readBytes, StandardCharsets.UTF_8);
    }

    public static void writeLELengthAsciiString(ByteBuf buffer, AsciiString string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        buffer.writeIntLE(string.length());
        buffer.writeBytes(string.toByteArray());
    }

    public static AsciiString readLELengthAsciiString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");

        int length = buffer.readIntLE();
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new AsciiString(bytes);
    }

    public static void writeBlockCoords(ByteBuf buf, Vector3i vector3i) {
        Varints.encodeSigned(buf, vector3i.getX());
        Varints.encodeUnsigned(buf, vector3i.getY());
        Varints.encodeSigned(buf, vector3i.getZ());
    }

    public static Vector3i readBlockCoords(ByteBuf buf) {
        int x = Varints.decodeSigned(buf);
        int y = (int) Varints.decodeUnsigned(buf);
        int z = Varints.decodeSigned(buf);
        return new Vector3i(x, y, z);
    }

    public static void writeVector3f(ByteBuf buf, Vector3f vector3f) {
        writeFloatLE(buf, vector3f.getX());
        writeFloatLE(buf, vector3f.getY());
        writeFloatLE(buf, vector3f.getZ());
    }

    public static Vector3f readVector3f(ByteBuf buf) {
        double x = readFloatLE(buf);
        double y = readFloatLE(buf);
        double z = readFloatLE(buf);
        return new Vector3f(x, y, z);
    }

    public static Collection<EntityAttribute> readEntityAttributes(ByteBuf buf) {
        List<EntityAttribute> attributes = new ArrayList<>();
        int size = (int) Varints.decodeUnsigned(buf);

        for (int i = 0; i < size; i++) {
            String name = readVarintLengthString(buf);
            float min = readFloatLE(buf);
            float max = readFloatLE(buf);
            float val = readFloatLE(buf);

            attributes.add(new EntityAttribute(name, min, max, val));
        }

        return attributes;
    }

    public static Collection<PlayerAttribute> readPlayerAttributes(ByteBuf buf) {
        List<PlayerAttribute> attributes = new ArrayList<>();
        int size = (int) Varints.decodeUnsigned(buf);

        for (int i = 0; i < size; i++) {
            float min = readFloatLE(buf);
            float max = readFloatLE(buf);
            float val = readFloatLE(buf);
            float defaultVal = readFloatLE(buf);
            String name = readVarintLengthString(buf);

            attributes.add(new PlayerAttribute(name, min, max, val, defaultVal));
        }

        return attributes;
    }

    public static void writeFloatLE(ByteBuf buf, float value) {
        buf.writeIntLE(Float.floatToRawIntBits(value));
    }

    public static float readFloatLE(ByteBuf buf) {
        return Float.intBitsToFloat(buf.readIntLE());
    }

    public static void writeEntityAttributes(ByteBuf buf, Collection<EntityAttribute> attributeList) {
        Varints.encodeUnsigned(buf, attributeList.size());
        for (EntityAttribute attribute : attributeList) {
            writeVarintLengthString(buf, attribute.getName());
            writeFloatLE(buf, attribute.getMinimumValue());
            writeFloatLE(buf, attribute.getMaximumValue());
            writeFloatLE(buf, attribute.getValue());
        }
    }

    public static void writePlayerAttributes(ByteBuf buf, Collection<PlayerAttribute> attributeList) {
        Varints.encodeUnsigned(buf, attributeList.size());
        for (PlayerAttribute attribute : attributeList) {
            writeFloatLE(buf, attribute.getMinimumValue());
            writeFloatLE(buf, attribute.getMaximumValue());
            writeFloatLE(buf, attribute.getValue());
            writeFloatLE(buf, attribute.getDefaultValue());
            writeVarintLengthString(buf, attribute.getName());
        }
    }

    public static void writeSkin(ByteBuf buf, Skin skin) {
        byte[] skinData = skin.getSkinData();
        byte[] capeData = skin.getCapeData();
        byte[] geometryData = skin.getGeometryData();
        writeVarintLengthString(buf, skin.getSkinId());
        Varints.encodeUnsigned(buf, skinData.length);
        buf.writeBytes(skinData);
        Varints.encodeUnsigned(buf, capeData.length);
        buf.writeBytes(capeData);
        writeVarintLengthString(buf, skin.getGeometryName());
        Varints.encodeUnsigned(buf, geometryData.length);
        buf.writeBytes(geometryData);
    }

    public static TranslatedMessage readTranslatedMessage(ByteBuf buf) {
        String message = readVarintLengthString(buf);
        int ln = buf.readByte();
        List<String> replacements = new ArrayList<>();
        for (int i = 0; i < ln; i++) {
            replacements.add(readVarintLengthString(buf));
        }
        return new TranslatedMessage(message, replacements);
    }

    public static void writeTranslatedMessage(ByteBuf buf, TranslatedMessage message) {
        writeVarintLengthString(buf, message.getName());
        buf.writeByte(message.getReplacements().size());
        for (String s : message.getReplacements()) {
            writeVarintLengthString(buf, s);
        }
    }

    public static ItemStack readItemStack(ByteBuf buf) {
        int id = Varints.decodeSigned(buf);
        if (id <= 0) {
            return new VoxelwindItemStack(BlockTypes.AIR, 1, null);
        }

        int aux = Varints.decodeSigned(buf);
        short damage = (short) (aux >> 8);
        if (damage == Short.MAX_VALUE) damage = -1;
        int count = aux & 0xff;
        short nbtSize = buf.readShortLE();

        ItemType type = ItemTypes.forId(id);

        ItemStackBuilder builder = new VoxelwindItemStackBuilder()
                .itemType(type)
                .itemData(MetadataSerializer.deserializeMetadata(type, damage))
                .amount(count);

        if (nbtSize > 0) {
            try (NBTReader reader = new NBTReader(new LittleEndianByteBufInputStream(buf.readSlice(nbtSize)))) {
                Tag<?> tag = reader.readTag();
                if (tag instanceof CompoundTag) {
                    VoxelwindNBTUtils.applyItemData(builder, ((CompoundTag) tag).getValue());
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load NBT data", e);
            }
        }

        int canPlace = Varints.decodeSigned(buf);
        for (int i = 0; i < canPlace; i++) {
            McpeUtil.readVarintLengthString(buf);
        }

        int canBreak = Varints.decodeSigned(buf);
        for (int i = 0; i < canBreak; i++) {
            McpeUtil.readVarintLengthString(buf);
        }
        return builder.build();
    }

    public static void writeItemStack(ByteBuf buf, ItemStack stack) {
        if (stack == null || stack.getItemType() == BlockTypes.AIR) {
            buf.writeByte(0); // 0 byte means 0 in varint
            return;
        }

        Varints.encodeSigned(buf, stack.getItemType().getId());
        short metadataValue = MetadataSerializer.serializeMetadata(stack);
        if (metadataValue == -1) metadataValue = Short.MAX_VALUE;
        Varints.encodeSigned(buf, (metadataValue << 8) | stack.getAmount());

        // Remember this position, since we'll be writing the true NBT size here later:
        int sizeIndex = buf.writerIndex();
        buf.writeShort(0);
        int afterSizeIndex = buf.writerIndex();

        if (stack instanceof VoxelwindItemStack) {
            try (NBTWriter stream = new NBTWriter(new LittleEndianByteBufOutputStream(buf))) {
                stream.write(((VoxelwindItemStack) stack).toSpecificNBT());
            } catch (IOException e) {
                // This shouldn't happen (as this is backed by a Netty ByteBuf), but okay...
                throw new IllegalStateException("Unable to save NBT data", e);
            }

            // Set to the written NBT size
            buf.setShortLE(sizeIndex, buf.writerIndex() - afterSizeIndex);
        }
        Varints.encodeSigned(buf, 0); // can place
        Varints.encodeSigned(buf, 0); // can break
    }

    public static UUID readUuid(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static Rotation readRotation(ByteBuf buffer) {
        float pitch = readFloatLE(buffer);
        float yaw = readFloatLE(buffer);
        float headYaw = readFloatLE(buffer);
        return new Rotation(pitch, yaw, headYaw);
    }

    public static void writeRotation(ByteBuf buffer, Rotation rotation) {
        writeFloatLE(buffer, rotation.getPitch());
        writeFloatLE(buffer, rotation.getYaw());
        writeFloatLE(buffer, rotation.getHeadYaw());
    }

    public static Rotation readByteRotation(ByteBuf buf) {
        byte pitchByte = buf.readByte();
        byte headYawByte = buf.readByte();
        byte yawByte = buf.readByte();
        return new Rotation(rotationByteToAngle(pitchByte), rotationByteToAngle(yawByte), rotationByteToAngle(headYawByte));
    }

    public static void writeByteRotation(ByteBuf buf, Rotation rotation) {
        buf.writeByte(rotationAngleToByte(rotation.getPitch()));
        buf.writeByte(rotationAngleToByte(rotation.getHeadYaw()));
        buf.writeByte(rotationAngleToByte(rotation.getYaw()));
    }

    private static byte rotationAngleToByte(float angle) {
        return (byte) Math.ceil(angle / 360 * 255);
    }

    private static float rotationByteToAngle(byte angle) {
        return angle / 255f * 360f;
    }

    public static void writeResourcePackInfo(ByteBuf buf, ResourcePackInfo info) {
        writeVarintLengthString(buf, info.getPackageId());
        writeVarintLengthString(buf, info.getVersion());
        buf.writeLong(info.getUnknown());
    }

    public static ResourcePackInfo readResourcePackInfo(ByteBuf buf) {
        String pid = readVarintLengthString(buf);
        String v = readVarintLengthString(buf);
        long unknown = buf.readLong();
        return new ResourcePackInfo(pid, v, unknown);
    }

    public static CommandOriginData readCommandOriginData(ByteBuf buf) {
        CommandOriginData.Origin origin = CommandOriginData.Origin.values()[(int) Varints.decodeUnsigned(buf)];
        UUID uuid = readUuid(buf);
        String requestId = readVarintLengthString(buf);
        Long varLong = null;
        if (origin == CommandOriginData.Origin.DEV_CONSOLE || origin == CommandOriginData.Origin.TEST) {
            varLong = Varints.decodeSignedLong(buf);
        }
        return new CommandOriginData(origin, uuid, requestId, varLong);
    }
}
