package arekkuusu.gsl.api.registry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class Effect implements INBTSerializable<CompoundNBT> {

    private final EffectType<?> type;

    public Effect(EffectType<?> type) {
        this.type = type;
    }

    public void remove() {}
    public void validate() {}
    public abstract void apply();

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

    public EffectType<?> getType() {
        return this.type;
    }
}
