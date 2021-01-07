package arekkuusu.gsl.api.network;

import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.api.util.GSLHelper;
import arekkuusu.gsl.api.util.WorldHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class RequestSkillUsePacket {

    public Skill<?> skill;
    public UUID uuid;

    public static void encoding(RequestSkillUsePacket msg, PacketBuffer buffer) {
        buffer.writeResourceLocation(msg.skill.getRegistryName());
        buffer.writeUniqueId(msg.uuid);
    }

    public static RequestSkillUsePacket decoding(PacketBuffer buffer) {
        RequestSkillUsePacket it = new RequestSkillUsePacket();
        it.skill = GSLRegistries.SKILLS.getValue(buffer.readResourceLocation());
        it.uuid = buffer.readUniqueId();
        return it;
    }

    public static void handle(RequestSkillUsePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GSLHelper.triggerSkillOn(WorldHelper.getEntityByUUID(msg.uuid), msg.skill);
        });
        ctx.get().setPacketHandled(true);
    }
}
