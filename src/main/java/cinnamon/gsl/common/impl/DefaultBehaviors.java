package cinnamon.gsl.common.impl;

import cinnamon.gsl.GSL;
import cinnamon.gsl.api.registry.BehaviorType;
import cinnamon.gsl.common.impl.behavior.*;
import net.minecraftforge.fml.RegistryObject;

public class DefaultBehaviors {

    public static final RegistryObject<BehaviorType<OnEndBehavior>> ON_END = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "on_end", () -> BehaviorType.builder().factory(OnEndBehavior::new).build()
    );
    public static final RegistryObject<BehaviorType<OnStartBehavior>> ON_START = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "on_start", () -> BehaviorType.builder().factory(OnStartBehavior::new).build()
    );
    public static final RegistryObject<BehaviorType<DefiniteBehavior>> DEFINITE = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "definite", () -> BehaviorType.builder().factory(DefiniteBehavior::new).build()
    );
    public static final RegistryObject<BehaviorType<IndefiniteBehavior>> INDEFINITE = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "indefinite", () -> BehaviorType.builder().factory(IndefiniteBehavior::new).build()
    );
    public static final RegistryObject<BehaviorType<WhileEntityLivesBehavior>> WHILE_ENTITY_LIVES = GSL.BEHAVIOR_TYPE_DEFERRED_REGISTER.register(
            "while_entity_lives", () -> BehaviorType.builder().factory(WhileEntityLivesBehavior::new).build()
    );

    public static void init() {
    }
}
