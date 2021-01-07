package arekkuusu.gsl.common;

import arekkuusu.gsl.GSL;
import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.registry.BehaviorType;
import arekkuusu.gsl.api.registry.EffectType;
import arekkuusu.gsl.api.registry.Skill;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

public class Registry {

    public static final ResourceLocation BEHAVIORS = new ResourceLocation(GSL.ID, "behavior");
    public static final ResourceLocation SKILLS = new ResourceLocation(GSL.ID, "skill");
    public static final ResourceLocation EFFECTS = new ResourceLocation(GSL.ID, "effect");
    public static final int MAX_INT = Integer.MAX_VALUE - 1;

    public static void init() {
        makeRegistry(SKILLS, Skill.class).create();
        makeRegistry(EFFECTS, EffectType.class).disableSaving().create();
        makeRegistry(BEHAVIORS, BehaviorType.class).disableSaving().create();
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type) {
        return new RegistryBuilder<T>().setName(name).setType(type).setMaxID(Registry.MAX_INT);
    }
}
