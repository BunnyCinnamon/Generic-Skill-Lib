package arekkuusu.gsl.common.impl;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.registry.EffectType;
import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.common.impl.example.EffectExample;
import arekkuusu.gsl.common.impl.example.SkillExample;
import net.minecraftforge.fml.RegistryObject;

public class ExamplesImpl {

    public static final RegistryObject<EffectType<EffectExample>> EXAMPLE_EFFECT = GSL.EFFECT_TYPE_DEFERRED_REGISTER.register(
            "example", () -> EffectType.Builder.create(EffectExample::new, DefaultBehaviors.EXAMPLE.get()).build()
    );
    public static final RegistryObject<Skill<SkillExample.ExampleData>> EXAMPLE_SKILL = GSL.SKILL_DEFERRED_REGISTER.register(
            "example", () -> new SkillExample(new Skill.Properties())
    );

    public static void init() {
    }
}
