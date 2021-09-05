package arekkuusu.gsl.api.registry;

import arekkuusu.gsl.api.registry.data.BehaviorContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class Behavior implements INBTSerializable<CompoundTag> {

    private final BehaviorType<?> type;

    public Behavior(BehaviorType<?> type) {
        this.type = type;
    }

    public abstract void update(BehaviorContext context);

    public abstract boolean isAlive();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        writeNBT(compound);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        readNBT(compound);
    }

    public abstract void writeNBT(CompoundTag compound);

    public abstract void readNBT(CompoundTag compound);

    public BehaviorType<?> getType() {
        return this.type;
    }
}
