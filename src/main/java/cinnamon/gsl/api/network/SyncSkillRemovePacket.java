package cinnamon.gsl.api.network;

import cinnamon.gsl.GSL;
import cinnamon.gsl.api.GSLCapabilities;
import cinnamon.gsl.api.GSLRegistries;
import cinnamon.gsl.api.registry.Skill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkillRemovePacket {

    public Skill<?> skill;

    public static void encoding(SyncSkillRemovePacket msg, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(msg.skill.getRegistryName());
    }

    public static SyncSkillRemovePacket decoding(FriendlyByteBuf buffer) {
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
