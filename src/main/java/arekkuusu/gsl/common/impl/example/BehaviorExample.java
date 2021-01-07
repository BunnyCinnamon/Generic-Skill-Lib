package arekkuusu.gsl.common.impl.example;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.DefaultBehaviors;
import net.minecraft.nbt.CompoundNBT;

public class BehaviorExample extends Behavior {

    public BehaviorExample() {
        super(DefaultBehaviors.EXAMPLE.get());
    }

    public int countDown;

    @Override
    public void update(BehaviorContext context) {
        if(countDown == 0) {
            context.effect.apply();
        }
        --countDown;//FAST
        --countDown;//FASTER
    }

    @Override
    public boolean isAlive() {
        return countDown >= 0;
    }

    @Override
    public void writeNBT(CompoundNBT compound) {
        compound.putInt("countDown", countDown);
    }

    @Override
    public void readNBT(CompoundNBT compound) {
        countDown = compound.getInt("countDown");
    }
}
