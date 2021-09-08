package arekkuusu.gsl.api;

import arekkuusu.gsl.api.registry.BehaviorType;
import arekkuusu.gsl.api.registry.EffectType;
import arekkuusu.gsl.api.registry.Skill;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class GSLRegistries {

    public static IForgeRegistry<Skill<?>> SKILLS = RegistryManager.ACTIVE.getRegistry(Skill.class);
    public static final IForgeRegistry<EffectType<?>> EFFECT_TYPES = RegistryManager.ACTIVE.getRegistry(EffectType.class);
    public static final IForgeRegistry<BehaviorType<?>> BEHAVIOR_TYPES = RegistryManager.ACTIVE.getRegistry(BehaviorType.class);
    public static final IForgeRegistry<EntityType<?>> ENTITY_TYPES = RegistryManager.ACTIVE.getRegistry(EntityType.class);
}
