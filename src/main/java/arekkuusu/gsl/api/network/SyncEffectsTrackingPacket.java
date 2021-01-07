package arekkuusu.gsl.api.network;

import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.util.WorldHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncEffectsTrackingPacket {

    public CompoundNBT nbt;
    public UUID uuid;

    public static void encoding(SyncEffectsTrackingPacket msg, PacketBuffer buffer) {
        buffer.writeCompoundTag(msg.nbt);
        buffer.writeUniqueId(msg.uuid);
    }

    public static SyncEffectsTrackingPacket decoding(PacketBuffer buffer) {
        SyncEffectsTrackingPacket it = new SyncEffectsTrackingPacket();
        it.nbt = buffer.readCompoundTag();
        it.uuid = buffer.readUniqueId();
        return it;
    }

    public static void handle(SyncEffectsTrackingPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.effect(WorldHelper.getEntityByUUID(msg.uuid)).ifPresent(c -> c.deserializeNBT(msg.nbt));
        });
        ctx.get().setPacketHandled(true);
    }
}
