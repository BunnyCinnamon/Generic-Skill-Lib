package arekkuusu.gsl.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;

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
        for (ServerLevel world : server.getAllLevels()) {
            Entity entity = world.getEntity(uuid);
            if (entity != null)
                return entity;
        }
        return null;
    }

    @Nullable
    public static Entity client(UUID uuid) {
        Minecraft client = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
        for (Entity entity : client.level.getEntities().getAll()) {
            if(entity.getUUID().equals(uuid))
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
            uuid = entity.getUUID();
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
