package cinnamon.gsl.common.impl.behavior;

import cinnamon.gsl.api.registry.Behavior;
import cinnamon.gsl.api.registry.BehaviorType;
import cinnamon.gsl.api.registry.data.BehaviorContext;
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
