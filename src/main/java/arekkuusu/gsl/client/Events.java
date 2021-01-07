package arekkuusu.gsl.client;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLCapabilities;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.capability.data.Skilled;
import arekkuusu.gsl.api.render.EffectRendererDispatcher;
import arekkuusu.gsl.api.render.SkillRendererDispatcher;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Iterator;

@EventBusSubscriber(modid = GSL.ID, value = Dist.CLIENT)
public class Events {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityTickActive(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().getEntityWorld().isRemote) {
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingPostRender(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
        if (event.getEntity() == Minecraft.getInstance().player
                && Minecraft.getInstance().gameSettings.thirdPersonView == 0)
            return;
        //
        float partial = event.getPartialRenderTick();
        MatrixStack stack = event.getMatrixStack();
        IRenderTypeBuffer buffer = event.getBuffers();
        //
        GSLCapabilities.effect(event.getEntity()).ifPresent(c -> {
            for (Affected affected : c.active.values()) {
                EffectRendererDispatcher.INSTANCE.getRender(affected.behaviorContext.effect)
                        .render(affected.behaviorContext.effect, partial, stack, buffer, event.getLight());
            }
        });
        GSLCapabilities.skill(event.getEntity()).ifPresent(c -> {
            for (Skilled skilled : c.skills.values()) {
                SkillRendererDispatcher.INSTANCE.getRender(skilled.skill)
                        .render(event.getEntity(), skilled.context, partial, stack, buffer, event.getLight());
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerScreenRender(RenderWorldLastEvent event) {
        if (Minecraft.getInstance().player == null
                || Minecraft.getInstance().getRenderManager().options == null
                || Minecraft.getInstance().gameSettings.thirdPersonView != 0)
            return;
        //
        float partial = event.getPartialTicks();
        MatrixStack stack = event.getMatrixStack();
        IRenderTypeBuffer buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        int light = Minecraft.getInstance().getRenderManager().getPackedLight(Minecraft.getInstance().player, event.getPartialTicks());
        //
        GSLCapabilities.effect(Minecraft.getInstance().player).ifPresent(c -> {
            for (Affected affected : c.active.values()) {
                EffectRendererDispatcher.INSTANCE.getRender(affected.behaviorContext.effect).render(affected.behaviorContext.effect, partial, stack, buffer, light);
            }
        });
        GSLCapabilities.skill(Minecraft.getInstance().player).ifPresent(c -> {
            for (Skilled skilled : c.skills.values()) {
                SkillRendererDispatcher.INSTANCE.getRender(skilled.skill).render(Minecraft.getInstance().player, skilled.context, partial, stack, buffer, light);
            }
        });
    }
}
