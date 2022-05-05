package cinnamon.gsl.common.impl;

import cinnamon.gsl.GSL;
import cinnamon.gsl.common.impl.entity.Strategic;
import cinnamon.gsl.common.impl.entity.StrategicBlocks;
import cinnamon.gsl.common.impl.entity.Throwable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.RegistryObject;

public class DefaultEntities {

    public static final RegistryObject<EntityType<Strategic>> STRATEGIC = GSL.ENTITY_TYPE_DEFERRED_REGISTER.register(
            "strategic", () -> EntityType.Builder.of(Strategic::new, MobCategory.MISC).sized(0.1F, 0.1F).build("strategic")
    );
    public static final RegistryObject<EntityType<StrategicBlocks>> STRATEGIC_BLOCKS = GSL.ENTITY_TYPE_DEFERRED_REGISTER.register(
            "strategic_blocks", () -> EntityType.Builder.of(StrategicBlocks::new, MobCategory.MISC).sized(0.5F, 0.5F).build("strategic_blocks")
    );
    public static final RegistryObject<EntityType<Throwable>> THROWABLE = GSL.ENTITY_TYPE_DEFERRED_REGISTER.register(
            "throwable", () -> EntityType.Builder.of(Throwable::new, MobCategory.MISC).sized(0.5F, 0.5F).build("throwable")
    );

    public static void init() {
    }
}
