package arekkuusu.gsl.common.impl.example;

import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.helper.GSLHelper;
import arekkuusu.gsl.api.helper.TeamHelper;
import arekkuusu.gsl.api.helper.TracerHelper;
import arekkuusu.gsl.api.helper.WorldHelper;
import arekkuusu.gsl.api.registry.Skill;
import arekkuusu.gsl.api.registry.data.SerDes;
import arekkuusu.gsl.common.impl.DefaultEntities;
import arekkuusu.gsl.common.impl.entity.StrategicDimensions;
import arekkuusu.gsl.common.impl.entity.data.EntityProperties;
import arekkuusu.gsl.common.impl.entity.data.EntityBehaviorInstances;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;

public class SkillExample extends Skill<SkillExample.ExampleData> {

    public SkillExample(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void use(LivingEntity owner, ExampleData context) {
        final var effect = ExamplesImpl.EXAMPLE_EFFECT.get().with(data -> {
            data.target = WorldHelper.WeakWorldReference.of((Player) owner);
            data.message = "Hi! #" + context.count++;
        });
        final var behavior = ExamplesImpl.EXAMPLE_BEHAVIOR.get().with(data -> {
            data.countDown = 60;
        });

        final var properties = new EntityProperties() {{

            final var affected = Affected.builder()
                    .of(effect)
                    .following(behavior)
                    .build(owner.getStringUUID());
            setEffect(affected);
            //
            setBehavior(EntityBehaviorInstances.EXPAND_EVENLY);
            setBehavior(EntityBehaviorInstances.SCAN_UNIQUE_BB);
            setTeamSelector(TeamHelper.TeamSelector.ENEMY);
            setDimensionsType(StrategicDimensions.Type.ON_HIT);
            setGrowthDelay(10);
            setDestroyDelay(0);
            setHeightInitial(0);
            setHeightFinal(5);
            setWidthInitial(0);
            setWidthFinal(5);
            setDuration(5);

        }};

        final var throwable = Objects.requireNonNull(DefaultEntities.THROWABLE.get().create(owner.level));
        throwable.setOwnerDirection(owner, TracerHelper.getLookedAt(owner, 10, TeamHelper.getSelectorAny()).getLocation());
        throwable.setEntitySpawn(DefaultEntities.STRATEGIC.get());
        throwable.setEntitySpawnProperties(properties);
        throwable.setFlyTime(5);
        throwable.setFlySpeed(0.2D);
        owner.level.addFreshEntity(throwable);
    }

    // This could then be @SubscribeEvent
    @SubscribeEvent
    public void onIdk(PlayerEvent.ItemPickupEvent event) {
        if (!event.getPlayer().level.isClientSide()) {
            if (!GSLHelper.isSkillOn(event.getPlayer(), ExamplesImpl.EXAMPLE_SKILL.get())) {
                GSLHelper.applySkillOn(event.getPlayer(), ExamplesImpl.EXAMPLE_SKILL.get());
            }
            GSLHelper.triggerSkillOn(event.getPlayer(), ExamplesImpl.EXAMPLE_SKILL.get());
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
