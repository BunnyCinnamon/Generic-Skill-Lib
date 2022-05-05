package cinnamon.gsl.api.registry;

import cinnamon.gsl.api.registry.data.SerDes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class Skill<T extends SerDes> extends ForgeRegistryEntry<Skill<?>> {

    private final Properties properties;

    public Skill(Properties properties) {
        this.properties = properties;
        if(this.properties.isHasEvents()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    public abstract void use(LivingEntity owner, T context);

    public abstract T create();

    public Properties getProperties() {
        return properties;
    }

    public static class Properties {

        boolean isKeyBound;
        boolean hasTexture;
        boolean hasEvents;

        public boolean isKeyBound() {
            return isKeyBound;
        }

        public Properties setKeyBound() {
            isKeyBound = true;
            return this;
        }

        public boolean isHasTexture() {
            return hasTexture;
        }

        public Properties setHasTexture() {
            this.hasTexture = true;
            return this;
        }

        public boolean isHasEvents() {
            return hasEvents;
        }

        public Properties setHasEvents() {
            return this;
        }
    }

    public static Class<Skill<?>> getType() {
        return (Class<Skill<?>>) (Object) Skill.class;
    }
}
