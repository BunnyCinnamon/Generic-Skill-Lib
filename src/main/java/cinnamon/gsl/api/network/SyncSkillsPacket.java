package cinnamon.gsl.api.network;

import cinnamon.gsl.api.GSLCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkillsPacket {

    public CompoundTag nbt;

    public static void encoding(SyncSkillsPacket msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.nbt);
    }

    public static SyncSkillsPacket decoding(FriendlyByteBuf buffer) {
        SyncSkillsPacket it = new SyncSkillsPacket();
        it.nbt = buffer.readNbt();
        return it;
    }

    public static void handle(SyncSkillsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.skill(ctx.get().getSender()).ifPresent(c -> c.deserializeNBT(msg.nbt));
        });
        ctx.get().setPacketHandled(true);
    }
}
