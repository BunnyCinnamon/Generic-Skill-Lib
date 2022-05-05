package cinnamon.gsl.common;

import cinnamon.gsl.GSL;
import cinnamon.gsl.api.GSLCapabilities;
import cinnamon.gsl.api.capability.data.Affected;
import cinnamon.gsl.api.GSLChannel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Iterator;

@EventBusSubscriber(modid = GSL.ID)
public class Events {

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            GSLChannel.sendSkillsSync((ServerPlayer) event.getEntity());
            GSLChannel.sendEffectsSync((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerTrackEntity(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity) {
            GSLChannel.sendEffectsSyncTracking((ServerPlayer) event.getPlayer(), (LivingEntity) event.getTarget());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getPlayer() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getPlayer();
            if (!event.isEndConquered()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityTickActive(LivingEvent.LivingUpdateEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            LivingEntity entity = event.getEntityLiving();
            GSLCapabilities.effect(entity).ifPresent(c -> {
                { //Iterate remove Effects
                    Iterator<Affected> it = c.queueRemove.iterator();
                    while (it.hasNext()) {
                        Affected affected = it.next();
                        c.active.remove(affected.id);
                        affected.behaviorContext.effect.invalidate();
                        it.remove();
                    }
                }
                { //Iterate add Effects
                    Iterator<Affected> it = c.queueAdd.iterator();
                    while (it.hasNext()) {
                        Affected affected = it.next();
                        c.active.put(affected.id, affected);
                        affected.behaviorContext.effect.validate();
                        it.remove();
                    }
                }
                for (Affected affected : c.active.values()) {
                    if (affected.behavior.isExecuting()) {
                        affected.behavior.execute(affected.behaviorContext);
                    } else {
                        c.queueRemove.add(affected);
                    }
                }
            });
        }
    }
}
