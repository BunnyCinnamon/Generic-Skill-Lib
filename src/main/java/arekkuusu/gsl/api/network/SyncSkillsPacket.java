package arekkuusu.gsl.api.network;

import arekkuusu.gsl.api.GSLCapabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkillsPacket {

    public CompoundNBT nbt;

    public static void encoding(SyncSkillsPacket msg, PacketBuffer buffer) {
        buffer.writeCompoundTag(msg.nbt);
    }

    public static SyncSkillsPacket decoding(PacketBuffer buffer) {
        SyncSkillsPacket it = new SyncSkillsPacket();
        it.nbt = buffer.readCompoundTag();
        return it;
    }

    public static void handle(SyncSkillsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.skill(ctx.get().getSender()).ifPresent(c -> c.deserializeNBT(msg.nbt));
        });
        ctx.get().setPacketHandled(true);
    }
}
