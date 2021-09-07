package arekkuusu.gsl.common.impl.entity.data;

import arekkuusu.gsl.api.GSLChannel;
import arekkuusu.gsl.api.helper.GSLHelper;
import arekkuusu.gsl.api.helper.TeamHelper;
import arekkuusu.gsl.common.impl.entity.Strategic;
import arekkuusu.gsl.common.impl.entity.StrategicDimensions;
import arekkuusu.gsl.common.impl.entity.StrategicDimensions.Weight;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;

import javax.annotation.Nullable;
import java.util.Map;

public class GSLStrategyInstances {
    public static final Int2ObjectMap<Strategy> ENTRIES = new Int2ObjectOpenHashMap<>();
    public static int id = 0;

    public static final Strategy NO_IMPLEMENT = new Strategy(id++) {
        @Override
        public EntityDimensions entityDimensions(Strategic strategic) {
            return StrategicDimensions.scalable(Weight.CENTER, 0.5F, 0.5F);
        }
    };

    public static final Strategy APPLY_EFFECT_ONCE_SPHERE = new Strategy(id++) {

        @Override
        public void tick(Strategic strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = TeamHelper.getEnemyTeamPredicate(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(!victims.containsKey(user) && victims.put(user, 0) != null) {
                    effects.forEach(affected -> {
                        GSLChannel.sendEffectAddSync(user, affected);
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }

        @Override
        public EntityDimensions entityDimensions(Strategic strategic) {
            return StrategicDimensions.scalable(Weight.CENTER, 0.5F, 0.5F);
        }
    };

    public static final Strategy APPLY_EFFECT_ONCE_FLAT = new Strategy(id++) {

        @Override
        public void tick(Strategic strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = TeamHelper.getEnemyTeamPredicate(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(!victims.containsKey(user) && victims.put(user, 0) != null) {
                    effects.forEach(affected -> {
                        GSLChannel.sendEffectAddSync(user, affected);
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

    public static final Strategy APPLY_EFFECT_TIMED_SPHERE = new Strategy(id++) {

        @Override
        public void tick(Strategic strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = TeamHelper.getEnemyTeamPredicate(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                Integer integer = victims.putIfAbsent(user, 0);
                if(integer != null && victims.replace(user, integer, ++integer)) {
                    effects.forEach(affected -> {
                        GSLChannel.sendEffectAddSync(user, affected);
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

    public static final Strategy APPLY_EFFECT_TIMED_FLAT = new Strategy(id++) {

        @Override
        public void tick(Strategic strategic) {
            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = TeamHelper.getEnemyTeamPredicate(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                Integer integer = victims.putIfAbsent(user, 0);
                if(integer != null && victims.replace(user, integer, ++integer)) {
                    effects.forEach(affected -> {
                        GSLChannel.sendEffectAddSync(user, affected);
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

    static {
        ENTRIES.put(NO_IMPLEMENT.getId(), NO_IMPLEMENT);
        ENTRIES.put(APPLY_EFFECT_ONCE_SPHERE.getId(), APPLY_EFFECT_ONCE_SPHERE);
        ENTRIES.put(APPLY_EFFECT_ONCE_FLAT.getId(), APPLY_EFFECT_ONCE_FLAT);
        ENTRIES.put(APPLY_EFFECT_TIMED_SPHERE.getId(), APPLY_EFFECT_TIMED_SPHERE);
        ENTRIES.put(APPLY_EFFECT_TIMED_FLAT.getId(), APPLY_EFFECT_TIMED_FLAT);
    }
}
