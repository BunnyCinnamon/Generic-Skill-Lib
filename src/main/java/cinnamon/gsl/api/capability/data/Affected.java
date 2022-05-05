package cinnamon.gsl.api.capability.data;

import cinnamon.gsl.api.registry.Behavior;
import cinnamon.gsl.api.registry.Effect;
import cinnamon.gsl.api.registry.data.BehaviorContext;

public final class Affected {

    public String id;
    public Behavior behavior;
    public BehaviorContext behaviorContext;

    public static <E extends Effect> Builder<E, ?> builder() {
        return new Builder<>();
    }

    public static class Builder<E extends Effect, B extends Behavior> {

        E effect;
        B behavior;

        public Builder<E, ?> of(E effect) {
            this.effect = effect;
            return this;
        }

        public <BB extends Behavior> Builder<E, BB> following(BB behavior) {
            @SuppressWarnings("unchecked") Builder<E, BB> cast = (Builder<E, BB>) this;
            if (!effect.getType().isValidBehavior(behavior.getType()))
                throw new IllegalArgumentException("Illegal behavior for effect");
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
