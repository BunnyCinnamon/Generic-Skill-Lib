package arekkuusu.gsl.common.impl.behavior;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.DefaultBehaviors;
import net.minecraft.nbt.CompoundTag;

public class DefiniteBehavior extends Behavior {

    public DefiniteBehavior() {
        super(DefaultBehaviors.EXAMPLE.get());
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
