package arekkuusu.gsl.api.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class Effect implements INBTSerializable<CompoundTag> {

    private final EffectType<?> type;

    public Effect(EffectType<?> type) {
        this.type = type;
    }

    public void remove() {}
    public void validate() {}
    public abstract void apply();

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

    public EffectType<?> getType() {
        return this.type;
    }
}
