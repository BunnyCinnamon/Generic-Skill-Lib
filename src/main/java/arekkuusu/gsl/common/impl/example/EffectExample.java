package arekkuusu.gsl.common.impl.example;

import arekkuusu.gsl.api.registry.Effect;
import arekkuusu.gsl.api.util.WorldHelper;
import arekkuusu.gsl.common.impl.ExamplesImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;

public class EffectExample extends Effect {

    public EffectExample() {
        super(ExamplesImpl.EXAMPLE_EFFECT.get());
    }

    public WorldHelper.WeakWorldReference<PlayerEntity> user;
    public String message;

    @Override
    public void apply() {
        PlayerEntity playerEntity = user.get();
        if (user.exists()) {
            playerEntity.sendStatusMessage(new StringTextComponent(message), true);
        }
    }

    @Override
    public void writeNBT(CompoundNBT compound) {
        compound.putString("message", message);
        compound.putUniqueId("uuid", user.getID());
    }

    @Override
    public void readNBT(CompoundNBT compound) {
        message = compound.getString("message");
        user = WorldHelper.WeakWorldReference.of(compound.getUniqueId("uuid"));
    }
}
