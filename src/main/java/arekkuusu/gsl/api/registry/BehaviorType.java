package arekkuusu.gsl.api.registry;

import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class BehaviorType<T extends Behavior> extends ForgeRegistryEntry<BehaviorType<?>> {

    private final Supplier<? extends T> factory;

    public BehaviorType(Supplier<? extends T> factory) {
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
        return this.factory.get();
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    public static final class Builder<T extends Behavior> {

        private Supplier<? extends T> factory;

        public <TT extends Behavior> Builder<TT> factory(Supplier<TT> factory) {
            this.factory = (Supplier<T>) factory;
            return (Builder<TT>) this;
        }

        public BehaviorType<T> build() {
            return new BehaviorType<>(this.factory);
        }
    }

    public static Class<BehaviorType<?>> getType() {
        return (Class<BehaviorType<?>>) (Object) BehaviorType.class;
    }
}