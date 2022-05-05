package cinnamon.gsl.common.impl.behavior;

import cinnamon.gsl.api.registry.Behavior;
import cinnamon.gsl.api.registry.BehaviorType;
import cinnamon.gsl.api.registry.data.BehaviorContext;
import net.minecraft.nbt.CompoundTag;

public class OnEndBehavior extends Behavior {

    public OnEndBehavior(BehaviorType<? extends Behavior> behavior) {
        super(behavior);
    }

    public int countDown;

    @Override
    public void execute(BehaviorContext context) {
        if(countDown == 0) {
            context.effect.apply();
        }
        --countDown;
    }

    @Override
    public boolean isExecuting() {
        return countDown >= 0;
    }

    @Override
    public void writeNBT(CompoundTag compound) {
        compound.putInt("countDown", countDown);
    }

    @Override
    public void readNBT(CompoundTag compound) {
        countDown = compound.getInt("countDown");
    }
}
