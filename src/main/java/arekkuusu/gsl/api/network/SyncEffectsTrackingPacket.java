package arekkuusu.gsl.api.network;

import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.helper.WorldHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncEffectsTrackingPacket {

    public CompoundTag nbt;
    public UUID uuid;

    public static void encoding(SyncEffectsTrackingPacket msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.nbt);
        buffer.writeUUID(msg.uuid);
    }

    public static SyncEffectsTrackingPacket decoding(FriendlyByteBuf buffer) {
        SyncEffectsTrackingPacket it = new SyncEffectsTrackingPacket();
        it.nbt = buffer.readNbt();
        it.uuid = buffer.readUUID();
        return it;
    }

    public static void handle(SyncEffectsTrackingPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.effect(WorldHelper.getEntityByUUID(msg.uuid)).ifPresent(c -> c.deserializeNBT(msg.nbt));
        });
        ctx.get().setPacketHandled(true);
    }
}
