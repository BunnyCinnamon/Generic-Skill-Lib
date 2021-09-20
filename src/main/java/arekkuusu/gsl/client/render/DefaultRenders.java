package arekkuusu.gsl.client.render;

import arekkuusu.gsl.api.registry.Effect;
import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.api.registry.data.SerDes;
import arekkuusu.gsl.api.render.EffectRenderer;
import arekkuusu.gsl.api.render.EffectRendererDispatcher;
import arekkuusu.gsl.api.render.SkillRenderer;
import arekkuusu.gsl.api.render.SkillRendererDispatcher;
import arekkuusu.gsl.common.impl.DefaultEntities;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public final class DefaultRenders {

    public static void init() {
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        //Abilities
        addEffectRenderer(Effect.class, new EffectRenderer<>() {
        }); //Fallback
        addSkillRenderer(Skill.class, new SkillRenderer<>() {
        }); //Fallback
        event.registerEntityRenderer(DefaultEntities.STRATEGIC.get(), c -> new EntityRenderer(c) {
            @Override
            public ResourceLocation getTextureLocation(Entity pEntity) {
                return null;
            }
        });
        event.registerEntityRenderer(DefaultEntities.STRATEGIC_BLOCKS.get(), c -> new EntityRenderer(c) {
            @Override
            public ResourceLocation getTextureLocation(Entity pEntity) {
                return null;
            }
        });
        event.registerEntityRenderer(DefaultEntities.THROWABLE.get(), c -> new EntityRenderer(c) {
            @Override
            public ResourceLocation getTextureLocation(Entity pEntity) {
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
