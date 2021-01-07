package arekkuusu.gsl.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public final class WorldHelper {

    @SuppressWarnings("unchecked")
    public static <E extends Entity> E getEntityByUUID(UUID uuid) {
        LogicalSide side = EffectiveSide.get();
        if (side == LogicalSide.SERVER) {
            return (E) server(uuid);
        } else {
            return (E) client(uuid);
        }
    }

    @Nullable
    public static Entity server(UUID uuid) {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        for (ServerWorld world : server.getWorlds()) {
            Entity entity = world.getEntityByUuid(uuid);
            if (entity != null)
                return entity;
        }
        return null;
    }

    @Nullable
    public static Entity client(UUID uuid) {
        Minecraft client = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
        for (Entity entity : client.world.getAllEntities()) {
            if(entity.getUniqueID().equals(uuid))
                return entity;
        }
        return null;
    }

    public static class WeakWorldReference<T extends Entity> {

        WeakReference<T> reference;
        UUID uuid;

        public T get() {
            if(reference == null || reference.get() == null) {
                reference = new WeakReference<>(WorldHelper.getEntityByUUID(uuid));
            }
            return reference.get();
        }

        public UUID getID() {
            return uuid;
        }

        public void setReference(T entity) {
            reference = new WeakReference<>(entity);
            uuid = entity.getUniqueID();
        }

        public boolean exists() {
            return reference != null && reference.get() != null;
        }

        public static <T extends Entity> WeakWorldReference<T> of(UUID uuid) {
            WeakWorldReference<T> ref = new WeakWorldReference<>();
            ref.uuid = uuid;
            return ref;
        }

        public static <T extends Entity> WeakWorldReference<T> of(T entity) {
            WeakWorldReference<T> ref = new WeakWorldReference<>();
            ref.setReference(entity);
            return ref;
        }
    }
}
