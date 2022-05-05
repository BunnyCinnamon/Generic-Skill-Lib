package cinnamon.gsl.client.render;

import cinnamon.gsl.api.registry.Effect;
import cinnamon.gsl.api.registry.Skill;
import cinnamon.gsl.api.registry.data.SerDes;
import cinnamon.gsl.api.render.EffectRenderer;
import cinnamon.gsl.api.render.EffectRendererDispatcher;
import cinnamon.gsl.api.render.SkillRenderer;
import cinnamon.gsl.api.render.SkillRendererDispatcher;
import cinnamon.gsl.common.impl.DefaultEntities;
import cinnamon.gsl.common.impl.entity.Strategic;
import cinnamon.gsl.common.impl.entity.Throwable;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public final class DefaultRenders {

    public static void init() {
        //Abilities
        addEffectRenderer(Effect.class, new EffectRenderer<>() {
        }); //Fallback
        addSkillRenderer(Skill.class, new SkillRenderer<>() {
        }); //Fallback
        RenderingRegistry.registerEntityRenderingHandler(DefaultEntities.STRATEGIC.get(), arg -> new EntityRenderer<>(arg) {
            @Override
            public ResourceLocation getTextureLocation(Strategic arg) {
                return null;
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(DefaultEntities.STRATEGIC_BLOCKS.get(), (IRenderFactory<Strategic>) arg -> new EntityRenderer<>(arg) {
            @Override
            public ResourceLocation getTextureLocation(Strategic arg) {
                return null;
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(DefaultEntities.THROWABLE.get(), arg -> new EntityRenderer<>(arg) {
            @Override
            public ResourceLocation getTextureLocation(Throwable arg) {
                return null;
            }
        });
    }

    public static <T extends Effect> void addEffectRenderer(Class<T> cl, EffectRenderer<T> render) {
        EffectRendererDispatcher.INSTANCE.add(cl, render);
    }

    public static <T extends Skill<E>, E extends SerDes> void addSkillRenderer(Class<T> cl, SkillRenderer<E> render) {
        SkillRendererDispatcher.INSTANCE.add(cl, render);
    }
}
