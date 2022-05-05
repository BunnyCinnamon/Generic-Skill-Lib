package cinnamon.gsl.api.network;

import cinnamon.gsl.GSL;
import cinnamon.gsl.api.GSLCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncEffectsPacket {

    public CompoundTag nbt;

    public static void encoding(SyncEffectsPacket msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.nbt);
    }

    public static SyncEffectsPacket decoding(FriendlyByteBuf buffer) {
        SyncEffectsPacket it = new SyncEffectsPacket();
        it.nbt = buffer.readNbt();
        return it;
    }

    public static void handle(SyncEffectsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.effect(GSL.getProxy().getPlayer()).ifPresent(c -> c.deserializeNBT(msg.nbt));
        });
        ctx.get().setPacketHandled(true);
    }
}
