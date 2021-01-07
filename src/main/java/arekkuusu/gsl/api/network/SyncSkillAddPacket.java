package arekkuusu.gsl.api.network;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.registry.Skill;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkillAddPacket {

    public Skill<?> skill;

    public static void encoding(SyncSkillAddPacket msg, PacketBuffer buffer) {
        buffer.writeResourceLocation(msg.skill.getRegistryName());
    }

    public static SyncSkillAddPacket decoding(PacketBuffer buffer) {
        SyncSkillAddPacket it = new SyncSkillAddPacket();
        it.skill = GSLRegistries.SKILLS.getValue(buffer.readResourceLocation());
        return it;
    }

    public static void handle(SyncSkillAddPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.skill(GSL.getProxy().getPlayer()).ifPresent(c -> c.add(msg.skill));
        });
        ctx.get().setPacketHandled(true);
    }
}
