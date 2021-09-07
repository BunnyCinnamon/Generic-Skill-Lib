package arekkuusu.gsl.common.impl.behavior;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.DefaultBehaviors;
import net.minecraft.nbt.CompoundTag;

public class OnStartBehavior extends Behavior {

    public OnStartBehavior() {
        super(DefaultBehaviors.ON_START.get());
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
