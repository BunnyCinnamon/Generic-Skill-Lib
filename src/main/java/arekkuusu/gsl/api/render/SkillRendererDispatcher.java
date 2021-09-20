package arekkuusu.gsl.api.render;

import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.api.registry.data.SerDes;
import com.google.common.collect.Maps;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class SkillRendererDispatcher {

    public static final SkillRendererDispatcher INSTANCE = new SkillRendererDispatcher();
    public final Map<Class<? extends Skill<?>>, SkillRenderer<? extends SerDes>> skillRendererMap = Maps.newHashMap();

    private SkillRendererDispatcher() {
        //Yoink!
    }

    public SkillRenderer<SerDes> getRenderClass(Class<Skill<? extends SerDes>> skill) {
        //noinspection unchecked
        SkillRenderer<SerDes> skillRenderer = (SkillRenderer<SerDes>) skillRendererMap.get(skill);
        if (skillRenderer == null) {
            //noinspection unchecked
            skillRenderer = SkillRendererDispatcher.INSTANCE.getRenderClass((Class<Skill<?>>) skill.getSuperclass());
            this.skillRendererMap.put(skill, skillRenderer);
        }
        return skillRenderer;
    }

    public SkillRenderer<SerDes> getRender(Skill<? extends SerDes> skill) {
        return getRenderClass((Class<Skill<? extends SerDes>>) skill.getClass());
    }

    public <T extends Skill<S>, S extends SerDes> void add(Class<T> skill, SkillRenderer<S> renderer) {
        SkillRendererDispatcher.INSTANCE.skillRendererMap.put(skill, renderer);
        renderer.setDispatcher(SkillRendererDispatcher.INSTANCE);
    }
}
