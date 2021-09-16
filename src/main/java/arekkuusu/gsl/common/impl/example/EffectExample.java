package arekkuusu.gsl.common.impl.example;

import arekkuusu.gsl.api.registry.Effect;
import arekkuusu.gsl.api.helper.WorldHelper;
import arekkuusu.gsl.api.registry.EffectType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class EffectExample extends Effect {

    public EffectExample(EffectType<? extends Effect> effectType) {
        super(effectType);
    }

    public WorldHelper.WeakWorldReference<Player> target;
    public String message;

    @Override
    public void apply() {
        Player playerEntity = target.get();
        if (target.exists()) {
            playerEntity.displayClientMessage(new TextComponent(message), true);
        }
    }

    @Override
    public void writeNBT(CompoundTag compound) {
        compound.putString("message", message);
        compound.putUUID("uuid", target.getID());
    }

    @Override
    public void readNBT(CompoundTag compound) {
        message = compound.getString("message");
        target = WorldHelper.WeakWorldReference.of(compound.getUUID("uuid"));
    }
}
