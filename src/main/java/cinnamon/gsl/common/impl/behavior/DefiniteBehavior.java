package cinnamon.gsl.common.impl.behavior;

import cinnamon.gsl.api.registry.Behavior;
import cinnamon.gsl.api.registry.BehaviorType;
import cinnamon.gsl.api.registry.data.BehaviorContext;
import net.minecraft.nbt.CompoundTag;

public class DefiniteBehavior extends Behavior {

    public DefiniteBehavior(BehaviorType<? extends Behavior> behavior) {
        super(behavior);
    }

    public int duration;
    int count;

    @Override
    public void execute(BehaviorContext context) {
        context.effect.apply();
        count++;
    }

    @Override
    public boolean isExecuting() {
        return count <= duration;
    }

    @Override
    public void writeNBT(CompoundTag compound) {
        compound.putInt("duration", duration);
        compound.putInt("count", count);
    }

    @Override
    public void readNBT(CompoundTag compound) {
        duration = compound.getInt("duration");
        count = compound.getInt("count");
    }
}
