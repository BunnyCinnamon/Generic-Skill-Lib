package arekkuusu.gsl.api.registry.data;

import arekkuusu.gsl.api.registry.Effect;
import arekkuusu.gsl.api.registry.EffectType;
import arekkuusu.gsl.api.util.NBTHelper;
import net.minecraft.nbt.CompoundNBT;

public class BehaviorContext extends SerDes {

    public Effect effect;

    public BehaviorContext(CompoundNBT tag) {
        super(tag);
    }

    public BehaviorContext(){}

    @Override
    public void writeNBT(CompoundNBT compound) {
        NBTHelper.setRegistry(compound, "effect", this.effect.getType());
        NBTHelper.setNBT(compound, "context", this.effect.serializeNBT());
    }

    @Override
    public void readNBT(CompoundNBT compound) {
        this.effect = NBTHelper.getRegistry(compound, "effect", EffectType.class).create();
        this.effect.deserializeNBT(NBTHelper.getNBTTag(compound, "context"));
    }
}
