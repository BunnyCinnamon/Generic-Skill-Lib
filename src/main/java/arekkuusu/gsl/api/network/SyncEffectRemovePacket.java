package arekkuusu.gsl.api.network;

import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.api.helper.WorldHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncEffectRemovePacket {

    public Affected affected;
    public UUID uuid;

    public static void encoding(SyncEffectRemovePacket msg, FriendlyByteBuf buffer) {
        buffer.writeUUID(msg.uuid);
        buffer.writeUtf(msg.affected.id);
        buffer.writeResourceLocation(msg.affected.behavior.getType().getRegistryName());
        buffer.writeNbt(msg.affected.behavior.serializeNBT());
        buffer.writeNbt(msg.affected.behaviorContext.serializeNBT());
    }

    public static SyncEffectRemovePacket decoding(FriendlyByteBuf buffer) {
        SyncEffectRemovePacket it = new SyncEffectRemovePacket();
        it.uuid = buffer.readUUID();
        it.affected = new Affected();
        it.affected.id = buffer.readUtf();
        it.affected.behavior = GSLRegistries.BEHAVIOR_TYPES.getValue(buffer.readResourceLocation()).create();
        it.affected.behavior.deserializeNBT(buffer.readNbt());
        it.affected.behaviorContext = new BehaviorContext();
        it.affected.behaviorContext.deserializeNBT(buffer.readNbt());
        return it;
    }

    public static void handle(SyncEffectRemovePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.effect(WorldHelper.getEntityByUUID(msg.uuid)).ifPresent(c -> c.queueRemove.add(msg.affected));
        });
        ctx.get().setPacketHandled(true);
    }
}
