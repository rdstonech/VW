package com.voxelwind.nbt.tags;

import com.voxelwind.nbt.util.CompoundTagBuilder;

import java.util.*;

public class CompoundTag implements Tag<Map<String, Tag<?>>> {
    private final String name;
    private final Map<String, Tag<?>> value;

    public CompoundTag(String name) {
        this.name = name;
        this.value = Collections.unmodifiableMap(new HashMap<>());
    }

    public CompoundTag(String name, Map<String, Tag<?>> value) {
        this.name = name;
        this.value = Collections.unmodifiableMap(new HashMap<>(value));
    }

    public void remove(String name) {
        if (value.containsKey(name)) {
            value.remove(name);
        }
    }

    public CompoundTag tag(Tag<?> tag) {
        value.put(tag.getName(), tag);
        return this;
    }

    public CompoundTag tagByte(String name, byte value) {
        return tag(new ByteTag(name, value));
    }

    public CompoundTag tagByteArray(String name, byte[] value) {
        return tag(new ByteArrayTag(name, value));
    }

    public CompoundTag tagDouble(String name, double value) {
        return tag(new DoubleTag(name, value));
    }

    public CompoundTag tagFloat(String name, float value) {
        return tag(new FloatTag(name, value));
    }

    public CompoundTag tagIntArray(String name, int[] value) {
        return tag(new IntArrayTag(name, value));
    }

    public CompoundTag tagInt(String name, int value) {
        return tag(new IntTag(name, value));
    }

    public CompoundTag tagLong(String name, long value) {
        return tag(new LongTag(name, value));
    }

    public CompoundTag tagShort(String name, short value) {
        return tag(new ShortTag(name, value));
    }

    public CompoundTag tagString(String name, String value) {
        return tag(new StringTag(name, value));
    }

    public CompoundTag tagCompoundTag(String name, CompoundTag value) {
        this.value.put(name, value);
        return this;
    }

    public CompoundTag tagListTag(String name, ListTag value) {
        this.value.put(name, value);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Tag<?>> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundTag that = (CompoundTag) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }

        StringBuilder bldr = new StringBuilder();
        bldr.append("TAG_Compound").append(append).append(": ").append(value.size()).append(" entries\r\n{\r\n");
        for (Tag entry : value.values()) {
            bldr.append("   ").append(entry.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        bldr.append("}");
        return bldr.toString();
    }

    public static CompoundTag createFromList(String name, List<Tag<?>> list) {
        Map<String, Tag<?>> map = new HashMap<>();
        for (Tag<?> tag : list) {
            if (tag.getName() == null || tag.getName().isEmpty()) {
                throw new IllegalArgumentException("Tag " + tag + " does not have a name.");
            }
            map.put(tag.getName(), tag);
        }
        return new CompoundTag(name, map);
    }

    public CompoundTagBuilder toBuilder() {
        return CompoundTagBuilder.from(this);
    }

    public Tag<?> get(String key) {
        return value.get(key);
    }
}
