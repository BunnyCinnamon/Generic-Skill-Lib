package arekkuusu.gsl.api.registry;

import arekkuusu.gsl.api.registry.data.BehaviorContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class Behavior implements INBTSerializable<CompoundNBT> {

    private final BehaviorType<?> type;

    public Behavior(BehaviorType<?> type) {
        this.type = type;
    }

    public abstract void update(BehaviorContext context);

    public abstract boolean isAlive();

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        writeNBT(compound);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        readNBT(compound);
    }

    public abstract void writeNBT(CompoundNBT compound);

    public abstract void readNBT(CompoundNBT compound);

    public BehaviorType<?> getType() {
        return this.type;
    }
}
