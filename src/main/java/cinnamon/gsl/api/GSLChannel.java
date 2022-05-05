package cinnamon.gsl.api;

import cinnamon.gsl.api.capability.AffectedCapability;
import cinnamon.gsl.api.capability.SkilledCapability;
import cinnamon.gsl.api.capability.data.Affected;
import cinnamon.gsl.api.network.*;
import cinnamon.gsl.api.registry.Skill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class GSLChannel {

    public static SimpleChannel INSTANCE;

    public static void sendSkillAddSync(ServerPlayer entity, Skill<?> skill) {
        SyncSkillAddPacket msg = new SyncSkillAddPacket();
        msg.skill = skill;
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendSkillRemoveSync(ServerPlayer entity, Skill<?> skill) {
        SyncSkillRemovePacket msg = new SyncSkillRemovePacket();
        msg.skill = skill;
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendSkillsSync(ServerPlayer entity) {
        SyncSkillsPacket msg = new SyncSkillsPacket();
        msg.nbt = GSLCapabilities.skill(entity).map(SkilledCapability::serializeNBT).orElseGet(CompoundTag::new);
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendEffectsSync(ServerPlayer entity) {
        SyncEffectsPacket msg = new SyncEffectsPacket();
        msg.nbt = GSLCapabilities.effect(entity).map(AffectedCapability::serializeNBT).orElseGet(CompoundTag::new);
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendEffectsSyncTracking(ServerPlayer entity, LivingEntity tracking) {
        SyncEffectsTrackingPacket msg = new SyncEffectsTrackingPacket();
        msg.nbt = GSLCapabilities.effect(tracking).map(AffectedCapability::serializeNBT).orElseGet(CompoundTag::new);
        msg.uuid = tracking.getUUID();
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), msg);
    }

    public static void sendEffectAddSync(LivingEntity entity, Affected affected) {
        SyncEffectAddPacket msg = new SyncEffectAddPacket();
        msg.affected = affected;
        msg.uuid = entity.getUUID();
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    public static void sendEffectRemoveSync(LivingEntity entity, Affected affected) {
        SyncEffectRemovePacket msg = new SyncEffectRemovePacket();
        msg.affected = affected;
        msg.uuid = entity.getUUID();
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    public static void sendSkillUseRequest(LivingEntity entity, Skill<?> skill) {
        RequestSkillUsePacket msg = new RequestSkillUsePacket();
        msg.skill = skill;
        msg.uuid = entity.getUUID();
        INSTANCE.sendToServer(msg);
    }
}
