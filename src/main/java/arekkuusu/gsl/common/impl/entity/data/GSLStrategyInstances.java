package arekkuusu.gsl.common.impl.entity.data;

import arekkuusu.gsl.api.helper.GSLHelper;
import arekkuusu.gsl.api.helper.TeamHelper;
import arekkuusu.gsl.common.impl.entity.Strategic;
import arekkuusu.gsl.common.impl.entity.StrategicBlocks;
import arekkuusu.gsl.common.impl.entity.StrategicDimensions;
import arekkuusu.gsl.common.impl.entity.StrategicDimensions.Weight;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.EntityDimensions;

public class GSLStrategyInstances {
    public static final Int2ObjectMap<Strategy<? extends Strategic>> ENTRIES = new Int2ObjectOpenHashMap<>();
    public static int id = 0;

    public static final Strategy<Strategic> NO_IMPLEMENT = new Strategy<>(id++) {
        @Override
        public EntityDimensions entityDimensions(Strategic strategic) {
            return StrategicDimensions.scalable(Weight.CENTER, 0.5F, 0.5F);
        }
    };

    public static final Strategy<Strategic> SPHERE_ONCE_CENTER = new Strategy<>(id++) {

        @Override
        public void tick(Strategic strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if (!victims.containsKey(user) && victims.put(user, 0) != null) {
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }

        @Override
        public EntityDimensions entityDimensions(Strategic strategic) {
            return StrategicDimensions.scalable(Weight.CENTER, strategic.getWidth() * 2.0F, strategic.getWidth() * 2.0F);
        }
    };

    public static final Strategy<Strategic> SPHERE_ONCE_BOTTOM = new Strategy<>(id++) {

        @Override
        public void tick(Strategic strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if (!victims.containsKey(user) && victims.put(user, 0) != null) {
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }

        @Override
        public EntityDimensions entityDimensions(Strategic strategic) {
            return StrategicDimensions.scalable(Weight.BOTTOM, strategic.getWidth() * 2.0F, strategic.getWidth() * 2.0F);
        }
    };

    public static final Strategy<Strategic> SPHERE_TIMED_CENTER = new Strategy<>(id++) {

        @Override
        public void tick(Strategic strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                Integer integer = victims.putIfAbsent(user, 0);
                if (integer != null && victims.replace(user, integer, ++integer)) {
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }

        @Override
        public EntityDimensions entityDimensions(Strategic strategic) {
            return StrategicDimensions.scalable(Weight.CENTER, strategic.getWidth() * 2.0F, strategic.getWidth() * 2.0F);
        }
    };

    public static final Strategy<Strategic> SPHERE_TIMED_BOTTOM = new Strategy<>(id++) {

        @Override
        public void tick(Strategic strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                Integer integer = victims.putIfAbsent(user, 0);
                if (integer != null && victims.replace(user, integer, ++integer)) {
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }

        @Override
        public EntityDimensions entityDimensions(Strategic strategic) {
            return StrategicDimensions.scalable(Weight.BOTTOM, strategic.getWidth() * 2.0F, strategic.getWidth() * 2.0F);
        }
    };

    public static final Strategy<StrategicBlocks> FLAT_TIMED_BOTTOM = new Strategy<>(id++) {

        @Override
        public void tick(StrategicBlocks strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                Integer integer = victims.putIfAbsent(user, 0);
                if (integer != null && victims.replace(user, integer, ++integer)) {
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }

        @Override
        public EntityDimensions entityDimensions(StrategicBlocks strategic) {
            return StrategicDimensions.scalable(Weight.BOTTOM, strategic.getWidth() * 2.0F, 0.5F);
        }
    };

    public static final Strategy<StrategicBlocks> FLAT_ONCE_BOTTOM = new Strategy<>(id++) {

        @Override
        public void tick(StrategicBlocks strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if (!victims.containsKey(user) && victims.put(user, 0) != null) {
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }

        @Override
        public EntityDimensions entityDimensions(StrategicBlocks strategic) {
            return StrategicDimensions.scalable(Weight.BOTTOM, strategic.getWidth() * 2.0F, 0.5F);
        }
    };

    static {
        ENTRIES.put(NO_IMPLEMENT.getId(), NO_IMPLEMENT);
        ENTRIES.put(SPHERE_ONCE_CENTER.getId(), SPHERE_ONCE_CENTER);
        ENTRIES.put(SPHERE_ONCE_BOTTOM.getId(), SPHERE_ONCE_BOTTOM);
        ENTRIES.put(SPHERE_TIMED_CENTER.getId(), SPHERE_TIMED_CENTER);
        ENTRIES.put(SPHERE_TIMED_BOTTOM.getId(), SPHERE_TIMED_BOTTOM);
        ENTRIES.put(FLAT_ONCE_BOTTOM.getId(), FLAT_ONCE_BOTTOM);
        ENTRIES.put(FLAT_TIMED_BOTTOM.getId(), FLAT_TIMED_BOTTOM);
    }
}
