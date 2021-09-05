package arekkuusu.gsl.common.impl.behavior;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.DefaultBehaviors;
import net.minecraft.nbt.CompoundTag;

public class IndefiniteBehavior extends Behavior {

    public IndefiniteBehavior() {
        super(DefaultBehaviors.EXAMPLE.get());
    }

    @Override
    public void update(BehaviorContext context) {
        context.effect.apply();
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void writeNBT(CompoundTag compound) {
    }

    @Override
    public void readNBT(CompoundTag compound) {
    }
}
