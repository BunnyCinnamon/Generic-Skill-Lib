package arekkuusu.gsl.common.impl.example;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.registry.BehaviorType;
import arekkuusu.gsl.api.registry.EffectType;
import arekkuusu.gsl.api.registry.Skill;
import net.minecraftforge.fmllegacy.RegistryObject;

public abstract class ExamplesImpl {

    public static final RegistryObject<BehaviorType<BehaviorExample>> EXAMPLE_BEHAVIOR = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "example", () -> BehaviorType.builder().factory(BehaviorExample::new).build()
    );
    public static final RegistryObject<EffectType<EffectExample>> EXAMPLE_EFFECT = GSL.EFFECT_TYPE_DEFERRED_REGISTER.register(
            "example", () -> EffectType.builder().factory(EffectExample::new).behaviors(ExamplesImpl.EXAMPLE_BEHAVIOR.get()).build()
    );
    public static final RegistryObject<Skill<SkillExample.ExampleData>> EXAMPLE_SKILL = GSL.SKILL_DEFERRED_REGISTER.register(
            "example", () -> new SkillExample(new Skill.Properties().setHasEvents())
    );

    public static void init() {
    }
}
