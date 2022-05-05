package cinnamon.gsl.api.helper;

import cinnamon.gsl.api.GSLAPI;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings({"Guava", "unchecked"})
public final class TeamHelper {

    private static final Function<Entity, Predicate<Entity>> ALLY = owner -> (entity -> owner instanceof Player
            ? (entity instanceof Player ? GSLAPI.defaultHumanTeam : (entity.getClassification(false) != MobCategory.MONSTER && GSLAPI.defaultAnimalTeam))
            : ((owner.getClassification(false) == MobCategory.MONSTER) == (entity.getClassification(false) == MobCategory.MONSTER))
    );
    private static final Function<Entity, Predicate<Entity>> ENEMY = owner -> (entity -> owner instanceof Player
            ? (entity instanceof Player ? !GSLAPI.defaultHumanTeam : (entity.getClassification(false) == MobCategory.MONSTER || !GSLAPI.defaultAnimalTeam))
            : ((owner.getClassification(false) == MobCategory.MONSTER) != (entity.getClassification(false) == MobCategory.MONSTER))
    );
    private static final Predicate<Entity> NOT_CREATIVE = entity -> !(entity instanceof Player) || !((Player) entity).isCreative() || !entity.isInvulnerable();
    private static final Function<Entity, Predicate<Entity>> SAME_TEAM = owner -> owner != null ? TeamHelper.getAllyTeamPredicate(owner) : Predicates.alwaysFalse();
    private static final Function<Entity, Predicate<Entity>> NOT_SAME_TEAM = owner -> owner != null ? TeamHelper.getEnemyTeamPredicate(owner) : Predicates.alwaysFalse();
    private static final Function<Entity, Predicate<Entity>> SELECTOR_ALLY = (owner) -> Predicates.or(Predicates.and(SAME_TEAM.apply(owner), NOT_CREATIVE), input -> input == owner);
    private static final Function<Entity, Predicate<Entity>> SELECTOR_ENEMY = (owner) -> Predicates.and(NOT_SAME_TEAM.apply(owner), NOT_CREATIVE, input -> input != owner);
    public static Class<LivingEntity> typeTest() {
        return LivingEntity.class;
    }

    private static <T extends Entity> Predicate<T> getAllyTeamPredicate(Entity owner) {
        return Predicates.and(target -> !target.isSpectator(), Predicates.or(target -> target.isAlliedTo(owner), ALLY.apply(owner)));
    }

    private static <T extends Entity> Predicate<T> getEnemyTeamPredicate(Entity owner) {
        return Predicates.and(target -> !target.isSpectator(), Predicates.and(target -> !target.isAlliedTo(owner), ENEMY.apply(owner)));
    }

    public static <T extends Entity> Predicate<T> getSelectorAny() {
        return target -> !target.isSpectator();
    }

    public static Predicate<Entity> getSelectorAlly(Entity owner) {
        return SELECTOR_ALLY.apply(owner);
    }

    public static Predicate<Entity> getSelectorEnemy(Entity owner) {
        return SELECTOR_ENEMY.apply(owner);
    }

    public enum TeamSelector implements StringRepresentable {
        ANY(ignore -> getSelectorAny()),
        ALLY(TeamHelper::getSelectorAlly),
        ENEMY(TeamHelper::getSelectorEnemy);

        final Function<Entity, Predicate<Entity>> function;

        TeamSelector(Function<Entity, Predicate<Entity>> function) {
            this.function = function;
        }

        @Override
        public String getSerializedName() {
            return name();
        }

        public Predicate<Entity> apply(Entity entity) {
            return this.function.apply(entity);
        }
    }
}
