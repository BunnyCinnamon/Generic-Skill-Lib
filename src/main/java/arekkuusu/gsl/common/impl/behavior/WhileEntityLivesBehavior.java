package arekkuusu.gsl.common.impl.behavior;

import arekkuusu.gsl.api.helper.WorldHelper;
import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.BehaviorType;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class WhileEntityLivesBehavior extends Behavior {

    public WhileEntityLivesBehavior(BehaviorType<? extends Behavior> behavior) {
        super(behavior);
    }

    public WorldHelper.WeakWorldReference<Player> user;

    @Override
    public void execute(BehaviorContext context) {
        context.effect.apply();
    }

    @Override
    public boolean isExecuting() {
        return user.get().isAlive();
    }

    @Override
    public void writeNBT(CompoundTag compound) {
        compound.putUUID("uuid", user.getID());
    }

    @Override
    public void readNBT(CompoundTag compound) {
        user = WorldHelper.WeakWorldReference.of(compound.getUUID("uuid"));
    }
}
