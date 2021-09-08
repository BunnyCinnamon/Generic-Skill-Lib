package arekkuusu.gsl.common.impl.behavior;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.BehaviorType;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import net.minecraft.nbt.CompoundTag;

public class IndefiniteBehavior extends Behavior {

    public IndefiniteBehavior(BehaviorType<? extends Behavior> behavior) {
        super(behavior);
    }

    @Override
    public void execute(BehaviorContext context) {
        context.effect.apply();
    }

    @Override
    public boolean isExecuting() {
        return true;
    }

    @Override
    public void writeNBT(CompoundTag compound) {
    }

    @Override
    public void readNBT(CompoundTag compound) {
    }
}
