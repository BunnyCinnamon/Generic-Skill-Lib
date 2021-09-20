package arekkuusu.gsl.api.render;

import arekkuusu.gsl.api.registry.Effect;
import com.google.common.collect.Maps;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class EffectRendererDispatcher {

    public static final EffectRendererDispatcher INSTANCE = new EffectRendererDispatcher();
    public final Map<Class<? extends Effect>, EffectRenderer<? extends Effect>> effectRendererMap = Maps.newHashMap();

    private EffectRendererDispatcher() {
        //Yoink!
    }

    public EffectRenderer<Effect> getRenderClass(Class<? extends Effect> effect) {
        //noinspection unchecked
        EffectRenderer<Effect> effectRenderer = (EffectRenderer<Effect>) effectRendererMap.get(effect);
        if (effectRenderer == null) {
            //noinspection unchecked
            effectRenderer = EffectRendererDispatcher.INSTANCE.getRenderClass((Class<? extends Effect>) effect.getSuperclass());
            this.effectRendererMap.put(effect, effectRenderer);
        }
        return effectRenderer;
    }

    public EffectRenderer<Effect> getRender(Effect effect) {
        return getRenderClass(effect.getClass());
    }

    public <T extends Effect> void add(Class<T> effect, EffectRenderer<T> renderer) {
        EffectRendererDispatcher.INSTANCE.effectRendererMap.put(effect, renderer);
        renderer.setDispatcher(EffectRendererDispatcher.INSTANCE);
    }
}
