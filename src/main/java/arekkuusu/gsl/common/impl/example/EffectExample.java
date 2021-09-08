package arekkuusu.gsl.common.impl.example;

import arekkuusu.gsl.api.registry.Effect;
import arekkuusu.gsl.api.helper.WorldHelper;
import arekkuusu.gsl.api.registry.EffectType;
import arekkuusu.gsl.common.impl.ExamplesImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class EffectExample extends Effect {

    public EffectExample(EffectType<? extends Effect> effectType) {
        super(effectType);
    }

    public WorldHelper.WeakWorldReference<Player> user;
    public String message;

    @Override
    public void apply() {
        Player playerEntity = user.get();
        if (user.exists()) {
            playerEntity.displayClientMessage(new TextComponent(message), true);
        }
    }

    @Override
    public void writeNBT(CompoundTag compound) {
        compound.putString("message", message);
        compound.putUUID("uuid", user.getID());
    }

    @Override
    public void readNBT(CompoundTag compound) {
        message = compound.getString("message");
        user = WorldHelper.WeakWorldReference.of(compound.getUUID("uuid"));
    }
}
