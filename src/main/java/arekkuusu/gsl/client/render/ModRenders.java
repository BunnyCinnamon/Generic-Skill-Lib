package arekkuusu.gsl.client.render;

import arekkuusu.gsl.api.registry.Effect;
import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.api.registry.data.SerDes;
import arekkuusu.gsl.api.render.EffectRenderer;
import arekkuusu.gsl.api.render.EffectRendererDispatcher;
import arekkuusu.gsl.api.render.SkillRenderer;
import arekkuusu.gsl.api.render.SkillRendererDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ModRenders {

    public static void init() {
        //Abilities
        registerEffect(Effect.class, new EffectRenderer<Effect>() {
        }); //Fallback
        registerSkill(Skill.class, new SkillRenderer<SerDes>() {
        }); //Fallback
    }

    public static <T extends Effect> void registerEffect(Class<T> cl, EffectRenderer<T> render) {
        EffectRendererDispatcher.INSTANCE.registerRenderer(cl, render);
    }

    public static <T extends Skill<E>, E extends SerDes> void registerSkill(Class<T> cl, SkillRenderer<E> render) {
        SkillRendererDispatcher.INSTANCE.registerRenderer(cl, render);
    }
}
