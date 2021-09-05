package arekkuusu.gsl.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public final class NBTHelper {

    /* ItemStack Fixer */
    public static CompoundTag fixNBT(ItemStack stack) {
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
            stack.setTag(tagCompound);
        }
        return tagCompound;
    }
    /* ItemStack Fixer */

    /* Basic Helpers */
    public static void putByte(CompoundTag compound, String tag, byte i) {
        compound.putByte(tag, i);
    }

    public static void putInt(CompoundTag compound, String tag, int i) {
        compound.putInt(tag, i);
    }

    public static void putFloat(CompoundTag compound, String tag, float i) {
        compound.putFloat(tag, i);
    }

    public static void putDouble(CompoundTag compound, String tag, double i) {
        compound.putDouble(tag, i);
    }

    public static void putBoolean(CompoundTag compound, String tag, boolean i) {
        compound.putBoolean(tag, i);
    }

    public static void putString(CompoundTag compound, String tag, String i) {
        compound.putString(tag, i);
    }

    public static void putUUID(CompoundTag compound, String tag, UUID i) {
        compound.putUUID(tag, i);
    }

    public static byte getByte(CompoundTag compound, String tag) {
        return compound.getByte(tag);
    }

    public static int getInt(CompoundTag compound, String tag) {
        return compound.getInt(tag);
    }

    public static float getFloat(CompoundTag compound, String tag) {
        return compound.getFloat(tag);
    }

    public static double getDouble(CompoundTag compound, String tag) {
        return compound.getDouble(tag);
    }

    public static boolean getBoolean(CompoundTag compound, String tag) {
        return compound.getBoolean(tag);
    }

    public static String getString(CompoundTag compound, String tag) {
        return compound.getString(tag);
    }

    @Nullable
    public static UUID getUUID(CompoundTag compound, String tag) {
        return compound.hasUUID(tag) ? compound.getUUID(tag) : null;
    }

    public static <T extends Tag> T setNBT(CompoundTag compound, String tag, T base) {
        compound.put(tag, base);
        return base;
    }

    public static boolean hasTag(CompoundTag compound, String tag, int type) {
        return compound != null && compound.contains(tag, type);
    }

    public static boolean hasTag(CompoundTag compound, String tag) {
        return compound != null && compound.contains(tag);
    }

    public static boolean hasUniqueID(CompoundTag compound, String tag) {
        return compound != null && compound.hasUUID(tag);
    }

    public static void removeTag(CompoundTag compound, String tag) {
        if (compound != null && compound.contains(tag)) {
            compound.remove(tag);
        }
    }
    /* Basic Helpers */

    /* Complex Helpers */
    public static void setArray(CompoundTag compound, String tag, String... array) {
        var list = new ListTag();
        for (String s : array) {
            list.add(StringTag.valueOf(s));
        }
        compound.put(tag, list);
    }

    public static String[] getArray(CompoundTag compound, String tag) {
        var list = compound.getList(tag, Constants.NBT.TAG_STRING);
        var array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.getString(i);
        }
        return array;
    }

    public static void setBlockPos(CompoundTag compound, String tag, @Nullable BlockPos pos) {
        if (pos == null) pos = BlockPos.ZERO;
        var nbt = new CompoundTag();
        nbt.putInt("x", pos.getX());
        nbt.putInt("y", pos.getY());
        nbt.putInt("z", pos.getZ());
        compound.put(tag, nbt);
    }

    public static BlockPos getBlockPos(CompoundTag compound, String tag) {
        var pos = BlockPos.ZERO;
        if (hasTag(compound, tag, Constants.NBT.TAG_COMPOUND)) {
            CompoundTag nbt = compound.getCompound(tag);
            var x = nbt.getInt("x");
            var y = nbt.getInt("y");
            var z = nbt.getInt("z");
            pos = new BlockPos(x, y, z);
        }
        return pos;
    }

    public static void setVector(CompoundTag compound, String tag, Vec3 vec) {
        var nbt = new CompoundTag();
        nbt.putDouble("x", vec.x);
        nbt.putDouble("y", vec.y);
        nbt.putDouble("z", vec.z);
        compound.put(tag, nbt);
    }

    public static Vec3 getVector(CompoundTag compound, String tag) {
        var vec = Vec3.ZERO;
        if (hasTag(compound, tag, Constants.NBT.TAG_COMPOUND)) {
            var nbt = compound.getCompound(tag);
            var x = nbt.getDouble("x");
            var y = nbt.getDouble("y");
            var z = nbt.getDouble("z");
            vec = new Vec3(x, y, z);
        }
        return vec;
    }

    public static <T extends IForgeRegistryEntry<T>> void setRegistry(CompoundTag compound, String tag, IForgeRegistryEntry<T> instance) {
        setResourceLocation(compound, tag, Objects.requireNonNull(instance.getRegistryName()));
    }

    public static <T extends IForgeRegistryEntry<T>> T getRegistry(CompoundTag compound, String tag, Class<T> registry) {
        ResourceLocation location = getResourceLocation(compound, tag);
        return GameRegistry.findRegistry(registry).getValue(location);
    }

    public static void setResourceLocation(CompoundTag compound, String tag, ResourceLocation location) {
        compound.putString(tag, location.toString());
    }

    public static ResourceLocation getResourceLocation(CompoundTag compound, String tag) {
        return new ResourceLocation(compound.getString(tag));
    }

    public static <T extends Enum<T> & StringRepresentable> void setEnum(CompoundTag compound, T t, String tag) {
        compound.putString(tag, t.getSerializedName());
    }

    public static <T extends Enum<T> & StringRepresentable> T getEnum(Class<T> clazz, CompoundTag compound, String tag) {
        String value = compound.getString(tag);
        return Stream.of(clazz.getEnumConstants()).filter(e -> e.getSerializedName().equals(value)).findAny().orElseGet(() -> clazz.getEnumConstants()[0]);
    }

    public static CompoundTag getNBTTag(CompoundTag compound, String tag) {
        return hasTag(compound, tag, Constants.NBT.TAG_COMPOUND) ? compound.getCompound(tag) : new CompoundTag();
    }

    public static ListTag getNBTList(CompoundTag compound, String tag) {
        return compound.getList(tag, Constants.NBT.TAG_COMPOUND);
    }
    /* Complex Helpers */
}
