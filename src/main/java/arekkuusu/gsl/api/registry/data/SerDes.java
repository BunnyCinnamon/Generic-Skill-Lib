package arekkuusu.gsl.api.registry.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class SerDes implements INBTSerializable<CompoundNBT> {

    public SerDes(CompoundNBT tag) {
        deserializeNBT(tag);
    }

    public SerDes() {
        //No-Op
    }

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
}
