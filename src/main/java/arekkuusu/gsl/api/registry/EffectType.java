package arekkuusu.gsl.api.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class EffectType<T extends Effect> extends ForgeRegistryEntry<EffectType<?>> {

    private final Supplier<? extends T> factory;
    private final Set<BehaviorType<?>> behaviors;

    public EffectType(Supplier<? extends T> factory, Set<BehaviorType<?>> behaviors) {
        this.factory = factory;
        this.behaviors = behaviors;
    }

    public boolean isValidBehavior(BehaviorType<?> behavior) {
        return this.behaviors.isEmpty() || this.behaviors.contains(behavior);
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

    public static final class Builder<T extends Effect> {
        private final Supplier<? extends T> factory;
        private final BehaviorType<?>[] behaviors;

        private Builder(Supplier<? extends T> factoryIn, BehaviorType<?>... behaviors) {
            this.factory = factoryIn;
            this.behaviors = behaviors;
        }

        public static <T extends Effect> EffectType.Builder<T> create(Supplier<? extends T> factoryIn, BehaviorType<?>... behaviors) {
            return new EffectType.Builder<>(factoryIn, behaviors);
        }

        public EffectType<T> build() {
            return new EffectType<T>(this.factory, ImmutableSet.copyOf(this.behaviors));
        }
    }

    public static Class<EffectType<?>> getType() {
        return (Class<EffectType<?>>) (Object) EffectType.class;
    }
}