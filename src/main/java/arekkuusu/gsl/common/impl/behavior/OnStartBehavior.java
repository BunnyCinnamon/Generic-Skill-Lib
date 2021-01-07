package arekkuusu.gsl.common.impl.behavior;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.DefaultBehaviors;
import net.minecraft.nbt.CompoundNBT;

public class OnStartBehavior extends Behavior {

    public OnStartBehavior() {
        super(DefaultBehaviors.ON_START.get());
    }

    private boolean alive = true;

    @Override
    public void update(BehaviorContext context) {
        if (alive) {
            context.effect.apply();
            alive = false;
        }
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void writeNBT(CompoundNBT compound) {
        compound.putBoolean("alive", alive);
    }

    @Override
    public void readNBT(CompoundNBT compound) {
        alive = compound.getBoolean("alive");
    }
}
