package arekkuusu.gsl.api.util;

import arekkuusu.gsl.api.GSLAPI;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.PlayerEntity;

@SuppressWarnings({"Guava", "ConstantConditions", "unchecked"})
public final class TeamHelper {

    private static final Function<Entity, Predicate<Entity>> SAME_TEAM = e -> e != null ? TeamHelper.getAllyTeamPredicate(e) : Predicates.alwaysFalse();
    private static final Function<Entity, Predicate<Entity>> NOT_SAME_TEAM = e -> e != null ? TeamHelper.getEnemyTeamPredicate(e) : Predicates.alwaysFalse();
    private static final Predicate<Entity> NOT_SPECTATING = e -> !e.isSpectator() && !e.noClip && e.canBeCollidedWith();
    private static final Predicate<Entity> NOT_CREATIVE = entity -> !(entity instanceof PlayerEntity) || !((PlayerEntity) entity).isCreative() || !((PlayerEntity) entity).abilities.disableDamage;
    private static final Predicate<Entity> HUMAN_TEAM = entity -> (GSLAPI.defaultHumanTeam && entity instanceof PlayerEntity) || (GSLAPI.defaultAnimalTeam && entity.getType().getClassification() != EntityClassification.MONSTER);
    private static final Predicate<Entity> NOT_HUMAN_TEAM = entity -> (!GSLAPI.defaultHumanTeam && entity instanceof PlayerEntity) || (!GSLAPI.defaultAnimalTeam || entity.getType().getClassification() == EntityClassification.MONSTER);

    public static final Function<Entity, Predicate<Entity>> SELECTOR_ALLY = (e) -> Predicates.or(Predicates.and(SAME_TEAM.apply(e), NOT_CREATIVE), input -> input == e);
    public static final Function<Entity, Predicate<Entity>> SELECTOR_ENEMY = (e) -> Predicates.and(NOT_SAME_TEAM.apply(e), NOT_CREATIVE, input -> input != e);

    public static <T extends Entity> Predicate<T> getAllyTeamPredicate(Entity owner) {
        return Predicates.and(TeamHelper.NOT_SPECTATING, Predicates.or(target -> target.isOnSameTeam(owner), HUMAN_TEAM));
    }

    public static <T extends Entity> Predicate<T> getEnemyTeamPredicate(Entity owner) {
        return Predicates.and(TeamHelper.NOT_SPECTATING, Predicates.and(target -> !target.isOnSameTeam(owner), NOT_HUMAN_TEAM));
    }
}
