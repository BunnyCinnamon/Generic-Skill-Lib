package cinnamon.gsl.common.impl.behavior;

import cinnamon.gsl.api.registry.Behavior;
import cinnamon.gsl.api.registry.BehaviorType;
import cinnamon.gsl.api.registry.data.BehaviorContext;
import net.minecraft.nbt.CompoundTag;

public class OnStartBehavior extends Behavior {

    public OnStartBehavior(BehaviorType<? extends Behavior> behavior) {
        super(behavior);
    }

    private boolean alive = true;

    @Override
    public void execute(BehaviorContext context) {
        if (alive) {
            context.effect.apply();
            alive = false;
        }
    }

    @Override
    public boolean isExecuting() {
        return alive;
    }

    @Override
    public void writeNBT(CompoundTag compound) {
        compound.putBoolean("alive", alive);
    }

    @Override
    public void readNBT(CompoundTag compound) {
        alive = compound.getBoolean("alive");
    }
}
