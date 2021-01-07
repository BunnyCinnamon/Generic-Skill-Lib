package arekkuusu.gsl.api.capability.data;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.Effect;
import arekkuusu.gsl.api.registry.data.BehaviorContext;

public final class Affected {

    public String id;
    public Behavior behavior;
    public BehaviorContext behaviorContext;

    public static class Builder<E extends Effect, B extends Behavior> {

        E effect;
        B behavior;

        public static <E extends Effect> Builder<E, ?> of(E effect) {
            Builder<E, ?> builder = new Builder<>();
            builder.effect = effect;
            return builder;
        }

        public <BB extends Behavior> Builder<E, BB> following(BB behavior) {
            @SuppressWarnings("unchecked") Builder<E, BB> cast = (Builder<E, BB>) this;
            if (!effect.getType().isValidBehavior(behavior.getType()))
                throw new IllegalArgumentException("Illegal behavor for effect");
            cast.behavior = behavior;
            return cast;
        }

        public Affected build(String id) {
            Affected holder = new Affected();
            holder.id = id;
            holder.behavior = behavior;
            holder.behaviorContext = new BehaviorContext();
            holder.behaviorContext.effect = effect;
            return holder;
        }
    }
}
