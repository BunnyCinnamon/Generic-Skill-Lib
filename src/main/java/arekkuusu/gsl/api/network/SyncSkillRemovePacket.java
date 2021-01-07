package arekkuusu.gsl.api.network;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.registry.Skill;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkillRemovePacket {

    public Skill<?> skill;

    public static void encoding(SyncSkillRemovePacket msg, PacketBuffer buffer) {
        buffer.writeResourceLocation(msg.skill.getRegistryName());
    }

    public static SyncSkillRemovePacket decoding(PacketBuffer buffer) {
        SyncSkillRemovePacket it = new SyncSkillRemovePacket();
        it.skill = GSLRegistries.SKILLS.getValue(buffer.readResourceLocation());
        return it;
    }

    public static void handle(SyncSkillRemovePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLCapabilities.skill(GSL.getProxy().getPlayer()).ifPresent(c -> c.remove(msg.skill));
        });
        ctx.get().setPacketHandled(true);
    }
}
