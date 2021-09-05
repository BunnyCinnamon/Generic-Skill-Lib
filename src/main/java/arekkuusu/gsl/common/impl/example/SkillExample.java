package arekkuusu.gsl.common.impl.example;

import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.registry.Behavior;
import arekkuusu.gsl.api.registry.Effect;
import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.api.registry.data.SerDes;
import arekkuusu.gsl.api.util.GSLHelper;
import arekkuusu.gsl.api.util.WorldHelper;
import arekkuusu.gsl.common.impl.DefaultBehaviors;
import arekkuusu.gsl.common.impl.ExamplesImpl;
import arekkuusu.gsl.api.GSLChannel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class SkillExample extends Skill<SkillExample.ExampleData> {

    public SkillExample(Properties properties) {
        super(properties);
    }

    @Override
    public void use(LivingEntity user, ExampleData context) {
        Effect effect = ExamplesImpl.EXAMPLE_EFFECT.get().with(d -> {
            d.message = "Hi! #" + context.count++;
            d.user = WorldHelper.WeakWorldReference.of((Player) user);
        });
        Behavior behavior = DefaultBehaviors.EXAMPLE.get().with(d -> {
            d.countDown = 60;
        });
        Affected affected = Affected.builder()
                .of(effect)
                .following(behavior)
                .build("example");

        GSLChannel.sendEffectAddSync(user, affected);
        GSLHelper.applyEffectOn(user, affected);
    }

    // This would then be @SubscribeEvent
    public void onIdk(PlayerEvent.ItemPickupEvent event) {
        if (!event.getPlayer().level.isClientSide()) {
            GSLHelper.triggerSkillOn(event.getPlayer(), this);
        }
    }

    @Override
    public ExampleData create() {
        return new ExampleData();
    }

    public static class ExampleData extends SerDes {

        public int count;

        @Override
        public void writeNBT(CompoundTag compound) {
            compound.putInt("count", count);
        }

        @Override
        public void readNBT(CompoundTag compound) {
            count = compound.getInt("count");
        }
    }
}
