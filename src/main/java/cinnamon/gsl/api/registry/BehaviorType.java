package cinnamon.gsl.api.registry;

import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class BehaviorType<T extends Behavior> extends ForgeRegistryEntry<BehaviorType<?>> {

    private final BehaviorFactory<T> factory;

    public BehaviorType(BehaviorFactory<T> factory) {
        this.factory = factory;
    }

    @Nonnull
    public T with(Consumer<T> consumer) {
        T t = create();
        consumer.accept(t);
        return t;
    }

    @Nonnull
    public T create() {
        return this.factory.create(this);
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    public static final class Builder<T extends Behavior> {

        private BehaviorFactory<T> factory;

        public <TT extends Behavior> Builder<TT> factory(BehaviorFactory<TT> factory) {
            this.factory = (BehaviorFactory<T>) factory;
            return (Builder<TT>) this;
        }

        public BehaviorType<T> build() {
            return new BehaviorType<T>(this.factory);
        }
    }

    public interface BehaviorFactory<T extends Behavior> {
        T create(BehaviorType<T> behaviorType);
    }

    public static Class<BehaviorType<?>> getType() {
        return (Class<BehaviorType<?>>) (Object) BehaviorType.class;
    }
}