package arekkuusu.gsl.api.network;

import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.api.util.WorldHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncEffectRemovePacket {

    public Affected affected;
    public UUID uuid;

    public static void encoding(SyncEffectRemovePacket msg, PacketBuffer buffer) {
        buffer.writeUniqueId(msg.uuid);
        buffer.writeString(msg.affected.id);
        buffer.writeResourceLocation(msg.affected.behavior.getType().getRegistryName());
        buffer.writeCompoundTag(msg.affected.behavior.serializeNBT());
        buffer.writeCompoundTag(msg.affected.behaviorContext.serializeNBT());
    }

    public static SyncEffectRemovePacket decoding(PacketBuffer buffer) {
        SyncEffectRemovePacket it = new SyncEffectRemovePacket();
        it.uuid = buffer.readUniqueId();
        it.affected = new Affected();
        it.affected.id = buffer.readString();
        it.affected.behavior = GSLRegistries.BEHAVIOR_TYPES.getValue(buffer.readResourceLocation()).create();
        it.affected.behavior.deserializeNBT(buffer.readCompoundTag());
        it.affected.behaviorContext = new BehaviorContext();
        it.affected.behaviorContext.deserializeNBT(buffer.readCompoundTag());
        return it;
    }

    public static void handle(SyncEffectRemovePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.effect(WorldHelper.getEntityByUUID(msg.uuid)).ifPresent(c -> c.queueRemove.add(msg.affected));
        });
        ctx.get().setPacketHandled(true);
    }
}
