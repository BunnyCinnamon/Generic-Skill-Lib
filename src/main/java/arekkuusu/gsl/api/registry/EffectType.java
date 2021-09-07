package arekkuusu.gsl.api.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
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

    public static final class Builder<T extends Effect> {

        private Supplier<T> factory;
        private BehaviorType<?>[] behaviors;

        public <TT extends Effect> Builder<TT> factory(Supplier<? extends TT> factory) {
            this.factory = (Supplier<T>) factory;
            return (Builder<TT>) this;
        }

        public Builder<T> behaviors(BehaviorType<?>... behaviors) {
            this.behaviors = behaviors;
            return this;
        }

        public EffectType<T> build() {
            return new EffectType<>(this.factory, ImmutableSet.copyOf(this.behaviors));
        }
    }

    public static Class<EffectType<?>> getType() {
        return (Class<EffectType<?>>) (Object) EffectType.class;
    }
}