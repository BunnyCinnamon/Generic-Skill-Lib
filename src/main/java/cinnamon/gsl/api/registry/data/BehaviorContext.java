package cinnamon.gsl.api.registry.data;

import cinnamon.gsl.api.registry.Effect;
import cinnamon.gsl.api.registry.EffectType;
import cinnamon.gsl.api.helper.NBTHelper;
import net.minecraft.nbt.CompoundTag;

public class BehaviorContext extends SerDes {

    public Effect effect;

    public BehaviorContext(CompoundTag tag) {
        super(tag);
    }

    public BehaviorContext(){}

    public void touch() {
        this.effect.apply();
    }

    @Override
    public void writeNBT(CompoundTag compound) {
        NBTHelper.putRegistry(compound, "effect", this.effect.getType());
        NBTHelper.putNBT(compound, "context", this.effect.serializeNBT());
    }

    @Override
    public void readNBT(CompoundTag compound) {
        this.effect = NBTHelper.getRegistry(compound, "effect", EffectType.class).create();
        this.effect.deserializeNBT(NBTHelper.getNBTTag(compound, "context"));
    }
}
