package cinnamon.gsl.api.network;

import cinnamon.gsl.api.GSLRegistries;
import cinnamon.gsl.api.helper.GSLHelper;
import cinnamon.gsl.api.helper.WorldHelper;
import cinnamon.gsl.api.registry.Skill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class RequestSkillUsePacket {

    public Skill<?> skill;
    public UUID uuid;

    public static void encoding(RequestSkillUsePacket msg, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(msg.skill.getRegistryName());
        buffer.writeUUID(msg.uuid);
    }

    public static RequestSkillUsePacket decoding(FriendlyByteBuf buffer) {
        RequestSkillUsePacket it = new RequestSkillUsePacket();
        it.skill = GSLRegistries.SKILLS.getValue(buffer.readResourceLocation());
        it.uuid = buffer.readUUID();
        return it;
    }

    public static void handle(RequestSkillUsePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLHelper.triggerSkillOn(WorldHelper.getEntityByUUID(msg.uuid), msg.skill);
        });
        ctx.get().setPacketHandled(true);
    }
}
