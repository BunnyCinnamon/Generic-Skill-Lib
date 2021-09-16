package arekkuusu.gsl.common.impl.entity;

import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.helper.NBTHelper;
import arekkuusu.gsl.api.helper.TeamHelper;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.entity.data.GSLDataSerializers;
import arekkuusu.gsl.common.impl.entity.data.GSLStrategyInstances;
import arekkuusu.gsl.common.impl.entity.data.Strategy;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Strategic extends Entity {

    private static final EntityDataAccessor<Float> DATA_WIDTH = SynchedEntityData.defineId(Strategic.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT = SynchedEntityData.defineId(Strategic.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(Strategic.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WAIT_TIME = SynchedEntityData.defineId(Strategic.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Strategy<Strategic>> DATA_STRATEGY = SynchedEntityData.defineId(Strategic.class, GSLDataSerializers.STRATEGY);
    private static final int TIME_BETWEEN_APPLICATIONS = 5;

    private final Map<Entity, Integer> victims = Maps.newHashMap();
    private final List<Affected> effects = Lists.newArrayList();
    private TeamHelper.TeamSelector teamSelector = TeamHelper.TeamSelector.ANY;
    @Nullable private LivingEntity owner;
    @Nullable private UUID ownerUUID;

    public Strategic(EntityType<? extends Strategic> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_WIDTH, 0.5F);
        this.getEntityData().define(DATA_HEIGHT, 0.5F);
        this.getEntityData().define(DATA_DURATION, 600);
        this.getEntityData().define(DATA_WAIT_TIME, 10);
        this.getEntityData().define(DATA_STRATEGY, GSLStrategyInstances.NO_IMPLEMENT);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= this.getWaitTime() + this.getDuration()) {
            this.discard();
            return;
        }
        if (this.level.isClientSide()) {
            this.getStrategy().particles(this);
        } else {
            if (this.tickCount % TIME_BETWEEN_APPLICATIONS == 0) {
                this.getStrategy().tick(this);
            }
        }
    }

    public void addEffect(Affected affected) {
        this.effects.add(affected);
    }

    public void addAllEffect(Collection<Affected> affected) {
        this.effects.addAll(affected);
    }

    public void setDuration(int duration) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_DURATION, Mth.clamp(duration, 0, 20));
        }
    }

    public void setWaitTime(int waitTime) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_WAIT_TIME, Mth.clamp(waitTime, 0, 10));
        }
    }

    public void setWidth(float pWidth) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_WIDTH, Mth.clamp(pWidth, 0.0F, 32.0F));
        }
    }

    public void setHeight(float pHeight) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_HEIGHT, Mth.clamp(pHeight, 0.0F, 32.0F));
        }
    }

    public void setTeamSelector(TeamHelper.TeamSelector teamSelector) {
        this.teamSelector = teamSelector;
    }

    public void setStrategy(Strategy strategy) {
        if (!this.level.isClientSide()) {
            this.getEntityData().set(DATA_STRATEGY, strategy);
        }
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public int getDuration() {
        return this.getEntityData().get(DATA_DURATION);
    }

    public int getWaitTime() {
        return this.getEntityData().get(DATA_WAIT_TIME);
    }

    public Map<Entity, Integer> getVictims() {
        return victims;
    }

    public List<Affected> getEffects() {
        return effects;
    }

    public float getWidth() {
        return this.getEntityData().get(DATA_WIDTH);
    }

    public float getHeight() {
        return this.getEntityData().get(DATA_HEIGHT);
    }

    public TeamHelper.TeamSelector getTeamSelector() {
        return teamSelector;
    }

    public Strategy<Strategic> getStrategy() {
        return this.getEntityData().get(DATA_STRATEGY);
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.tickCount = pCompound.getInt("Age");
        this.setDuration(pCompound.getInt("Duration"));
        this.setWaitTime(pCompound.getInt("WaitTime"));
        this.setWidth(pCompound.getFloat("Width"));
        this.setHeight(pCompound.getFloat("Height"));
        this.setTeamSelector(NBTHelper.getEnum(TeamHelper.TeamSelector.class, pCompound, "TeamSelector"));
        this.setStrategy(GSLStrategyInstances.ENTRIES.get(pCompound.getInt("Strategy")));
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }

        if (pCompound.contains("Effects", 9)) {
            ListTag listtag = pCompound.getList("Effects", Tag.TAG_COMPOUND);
            this.effects.clear();

            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag tag = listtag.getCompound(i);
                Affected affected = new Affected();
                affected.id = tag.getString("Id");
                affected.behavior = GSLRegistries.BEHAVIOR_TYPES.getValue(new ResourceLocation(tag.getString("Resource"))).create();
                affected.behavior.deserializeNBT(tag.getCompound("Behavior"));
                affected.behaviorContext = new BehaviorContext();
                affected.behaviorContext.deserializeNBT(tag.getCompound("BehaviorContext"));
                this.addEffect(affected);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Age", this.tickCount);
        pCompound.putInt("Duration", this.getDuration());
        pCompound.putInt("WaitTime", this.getWaitTime());
        pCompound.putFloat("Width", this.getWidth());
        pCompound.putFloat("Height", this.getHeight());
        NBTHelper.setEnum(pCompound, "TeamSelector", this.getTeamSelector());
        pCompound.putInt("Strategy", this.getStrategy().getId());
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }

        if (!this.effects.isEmpty()) {
            ListTag listtag = new ListTag();

            for(Affected affected : this.effects) {
                CompoundTag tag = new CompoundTag();
                tag.putString("Id", affected.id);
                tag.putString("Resource", affected.behavior.getType().getRegistryName().toString());
                tag.put("Behavior", affected.behavior.serializeNBT());
                tag.put("BehaviorContext", affected.behaviorContext.serializeNBT());
            }

            pCompound.put("Effects", listtag);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_WIDTH.equals(pKey)) {
            this.refreshDimensions();
        }
        if (DATA_HEIGHT.equals(pKey)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.getStrategy().entityDimensions(this);
    }
}
