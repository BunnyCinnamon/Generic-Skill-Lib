package arekkuusu.gsl.common.impl.behavior;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.DefaultBehaviors;
import net.minecraft.nbt.CompoundNBT;

public class DefiniteBehavior extends Behavior {

    public DefiniteBehavior() {
        super(DefaultBehaviors.EXAMPLE.get());
    }

    public int duration;
    int count;

    @Override
    public void update(BehaviorContext context) {
        context.effect.apply();
        count++;
    }

    @Override
    public boolean isAlive() {
        return count <= duration;
    }

    @Override
    public void writeNBT(CompoundNBT compound) {
        compound.putInt("duration", duration);
        compound.putInt("count", count);
    }

    @Override
    public void readNBT(CompoundNBT compound) {
        duration = compound.getInt("duration");
        count = compound.getInt("count");
    }
}
