package arekkuusu.gsl.common.impl;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.registry.BehaviorType;
import arekkuusu.gsl.common.impl.behavior.*;
import arekkuusu.gsl.common.impl.example.BehaviorExample;
import net.minecraftforge.fml.RegistryObject;

public class DefaultBehaviors {

    public static final RegistryObject<BehaviorType<BehaviorExample>> EXAMPLE = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "example", () -> BehaviorType.Builder.create(BehaviorExample::new).build()
    );
    public static final RegistryObject<BehaviorType<OnEndBehavior>> ON_END = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "on_end", () -> BehaviorType.Builder.create(OnEndBehavior::new).build()
    );
    public static final RegistryObject<BehaviorType<OnStartBehavior>> ON_START = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "on_start", () -> BehaviorType.Builder.create(OnStartBehavior::new).build()
    );
    public static final RegistryObject<BehaviorType<DefiniteBehavior>> DEFINITE = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "definite", () -> BehaviorType.Builder.create(DefiniteBehavior::new).build()
    );
    public static final RegistryObject<BehaviorType<WhileEntityLivesBehavior>> WHILE_ENTITY_LIVES = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "while_entity_lives", () -> BehaviorType.Builder.create(WhileEntityLivesBehavior::new).build()
    );
    public static final RegistryObject<BehaviorType<IndefiniteBehavior>> INDEFINITE = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "indefinite", () -> BehaviorType.Builder.create(IndefiniteBehavior::new).build()
    );

    public static void init() {
    }
}
