package arekkuusu.gsl.api.network;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLCapabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncEffectsPacket {

    public CompoundNBT nbt;

    public static void encoding(SyncEffectsPacket msg, PacketBuffer buffer) {
        buffer.writeCompoundTag(msg.nbt);
    }

    public static SyncEffectsPacket decoding(PacketBuffer buffer) {
        SyncEffectsPacket it = new SyncEffectsPacket();
        it.nbt = buffer.readCompoundTag();
        return it;
    }

    public static void handle(SyncEffectsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.effect(GSL.getProxy().getPlayer()).ifPresent(c -> c.deserializeNBT(msg.nbt));
        });
        ctx.get().setPacketHandled(true);
    }
}
