package arekkuusu.gsl.api.registry.data;

import arekkuusu.gsl.api.registry.Effect;
import arekkuusu.gsl.api.registry.EffectType;
import arekkuusu.gsl.api.helper.NBTHelper;
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
        NBTHelper.setRegistry(compound, "effect", this.effect.getType());
        NBTHelper.setNBT(compound, "context", this.effect.serializeNBT());
    }

    @Override
    public void readNBT(CompoundTag compound) {
        this.effect = NBTHelper.getRegistry(compound, "effect", EffectType.class).create();
        this.effect.deserializeNBT(NBTHelper.getNBTTag(compound, "context"));
    }
}
