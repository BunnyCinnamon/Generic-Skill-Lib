package arekkuusu.gsl.api.registry;

import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class BehaviorType<T extends Behavior> extends ForgeRegistryEntry<BehaviorType<?>> {

    private final Supplier<? extends T> factory;

    public BehaviorType(Supplier<? extends T> factory) {
        this.factory = factory;
    }

    @Nullable
    public T with(Consumer<T> consumer) {
        T t = create();
        consumer.accept(t);
        return t;
    }

    @Nullable
    public T create() {
        return this.factory.get();
    }

    public static final class Builder<T extends Behavior> {
        private final Supplier<? extends T> factory;

        private Builder(Supplier<? extends T> factoryIn) {
            this.factory = factoryIn;
        }

        public static <T extends Behavior> BehaviorType.Builder<T> create(Supplier<? extends T> factoryIn) {
            return new BehaviorType.Builder<>(factoryIn);
        }

        public BehaviorType<T> build() {
            return new BehaviorType<>(this.factory);
        }
    }

    public static Class<BehaviorType<?>> getType() {
        return (Class<BehaviorType<?>>) (Object) BehaviorType.class;
    }
}