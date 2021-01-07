package arekkuusu.gsl.common.impl.behavior;

import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.api.util.WorldHelper;
import arekkuusu.gsl.common.impl.DefaultBehaviors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class WhileEntityLivesBehavior extends Behavior {

    public WhileEntityLivesBehavior() {
        super(DefaultBehaviors.EXAMPLE.get());
    }

    public WorldHelper.WeakWorldReference<PlayerEntity> user;

    @Override
    public void update(BehaviorContext context) {
        context.effect.apply();
    }

    @Override
    public boolean isAlive() {
        return user.get().isAlive();
    }

    @Override
    public void writeNBT(CompoundNBT compound) {
        compound.putUniqueId("uuid", user.getID());
    }

    @Override
    public void readNBT(CompoundNBT compound) {
        user = WorldHelper.WeakWorldReference.of(compound.getUniqueId("uuid"));
    }
}
