package arekkuusu.gsl.common;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.GSLChannel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
        if (event.getEntity() instanceof ServerPlayerEntity) {
            GSLChannel.sendSkillsSync((ServerPlayerEntity) event.getEntity());
            GSLChannel.sendEffectsSync((ServerPlayerEntity) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerTrackEntity(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity) {
            GSLChannel.sendEffectsSyncTracking((ServerPlayerEntity) event.getPlayer(), (LivingEntity) event.getTarget());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            if (!event.isEndConquered()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityTickActive(LivingEvent.LivingUpdateEvent event) {
        if (!event.getEntityLiving().getEntityWorld().isRemote) {
            LivingEntity entity = event.getEntityLiving();
            GSLCapabilities.effect(entity).ifPresent(c -> {
                { //Iterate remove Effects
                    Iterator<Affected> it = c.queueRemove.iterator();
                    while (it.hasNext()) {
                        Affected affected = it.next();
                        c.active.remove(affected.id);
                        affected.behaviorContext.effect.remove();
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
                    if (affected.behavior.isAlive()) {
                        affected.behavior.update(affected.behaviorContext);
                    } else {
                        c.queueRemove.add(affected);
                    }
                }
            });
        }
    }
}
