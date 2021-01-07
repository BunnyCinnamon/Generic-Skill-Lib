package arekkuusu.gsl.api;

import arekkuusu.gsl.api.capability.AffectedCapability;
import arekkuusu.gsl.api.capability.SkilledCapability;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.network.*;
import arekkuusu.gsl.api.registry.Skill;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class GSLChannel {

    public static SimpleChannel INSTANCE;

    public static void sendSkillAddSync(ServerPlayerEntity entity, Skill<?> skill) {
        SyncSkillAddPacket msg = new SyncSkillAddPacket();
        msg.skill = skill;
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendSkillRemoveSync(ServerPlayerEntity entity, Skill<?> skill) {
        SyncSkillRemovePacket msg = new SyncSkillRemovePacket();
        msg.skill = skill;
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendSkillsSync(ServerPlayerEntity entity) {
        SyncSkillsPacket msg = new SyncSkillsPacket();
        msg.nbt = GSLCapabilities.skill(entity).map(SkilledCapability::serializeNBT).orElseGet(CompoundNBT::new);
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendEffectsSync(ServerPlayerEntity entity) {
        SyncEffectsPacket msg = new SyncEffectsPacket();
        msg.nbt = GSLCapabilities.effect(entity).map(AffectedCapability::serializeNBT).orElseGet(CompoundNBT::new);
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendEffectsSyncTracking(ServerPlayerEntity entity, LivingEntity tracking) {
        SyncEffectsTrackingPacket msg = new SyncEffectsTrackingPacket();
        msg.nbt = GSLCapabilities.effect(tracking).map(AffectedCapability::serializeNBT).orElseGet(CompoundNBT::new);
        msg.uuid = tracking.getUniqueID();
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendEffectAddSync(LivingEntity entity, Affected affected) {
        SyncEffectAddPacket msg = new SyncEffectAddPacket();
        msg.affected = affected;
        msg.uuid = entity.getUniqueID();
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    public static void sendEffectRemoveSync(LivingEntity entity, Affected affected) {
        SyncEffectRemovePacket msg = new SyncEffectRemovePacket();
        msg.affected = affected;
        msg.uuid = entity.getUniqueID();
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    public static void sendSkillUseRequest(LivingEntity entity, Skill<?> skill) {
        RequestSkillUsePacket msg = new RequestSkillUsePacket();
        msg.skill = skill;
        msg.uuid = entity.getUniqueID();
        INSTANCE.sendToServer(msg);
    }
}
