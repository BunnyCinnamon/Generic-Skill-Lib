package arekkuusu.gsl.common.impl.example;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.BehaviorType;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.DefaultBehaviors;
import net.minecraft.nbt.CompoundTag;

public class BehaviorExample extends Behavior {

    public BehaviorExample(BehaviorType<? extends Behavior> behavior) {
        super(behavior);
    }

    public int countDown;

    @Override
    public void execute(BehaviorContext context) {
        if(countDown == 0) {
            context.effect.apply();
        }
        --countDown;//FAST
        --countDown;//FASTER
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
