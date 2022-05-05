package cinnamon.gsl.api.registry.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class SerDes implements INBTSerializable<CompoundTag> {

    public SerDes(CompoundTag tag) {
        deserializeNBT(tag);
    }

    public SerDes() {
        //No-Op
    }

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
}
