package cinnamon.gsl.api.network;

import cinnamon.gsl.GSL;
import cinnamon.gsl.api.GSLCapabilities;
import cinnamon.gsl.api.GSLRegistries;
import cinnamon.gsl.api.registry.Skill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

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
