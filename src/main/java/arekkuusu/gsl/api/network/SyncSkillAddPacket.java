package arekkuusu.gsl.api.network;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.registry.Skill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkillAddPacket {

    public Skill<?> skill;

    public static void encoding(SyncSkillAddPacket msg, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(msg.skill.getRegistryName());
    }

    public static SyncSkillAddPacket decoding(FriendlyByteBuf buffer) {
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
