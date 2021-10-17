package arekkuusu.gsl.common.impl.entity;

import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.helper.NBTHelper;
import arekkuusu.gsl.api.helper.TeamHelper;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.entity.data.EntityBehavior;
import arekkuusu.gsl.common.impl.entity.data.EntityBehaviorInstances;
import arekkuusu.gsl.common.impl.entity.data.EntityDataSerializers;
import arekkuusu.gsl.common.impl.entity.data.EntityProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Strategic extends Entity {

    private static final EntityDataAccessor<Float> DATA_WIDTH = SynchedEntityData.defineId(Strategic.class, net.minecraft.network.syncher.EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT = SynchedEntityData.defineId(Strategic.class, net.minecraft.network.syncher.EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(Strategic.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_GROWTH_DELAY = SynchedEntityData.defineId(Strategic.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
    private static final EntityDataAccessor<Set<EntityBehavior<? extends Strategic>>> DATA_BEHAVIORS = SynchedEntityData.defineId(Strategic.class, EntityDataSerializers.STRATEGY);

    private final Map<UUID, Integer> victims = Maps.newHashMap();
    private final List<Affected> effects = Lists.newArrayList();
    private TeamHelper.TeamSelector teamSelector = TeamHelper.TeamSelector.ANY;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    private float widthInitial, heightInitial;
    private float widthFinal, heightFinal;

    public Strategic(EntityType<? extends Strategic> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_WIDTH, 0.5F);
        this.getEntityData().define(DATA_HEIGHT, 0.5F);
        this.getEntityData().define(DATA_DURATION, 600);
        this.getEntityData().define(DATA_GROWTH_DELAY, 10);
        this.getEntityData().define(DATA_BEHAVIORS, Sets.newHashSet());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= this.getGrowthDelay() + this.getDuration()) {
            this.discard();
            return;
        }
        for (EntityBehavior<? extends Strategic> strategy : this.getBehaviors()) {
            ((EntityBehavior<Strategic>) strategy).tick(this);
        }
    }

    public void applyProperties(EntityProperties<? extends Strategic> properties) {
        this.setGrowthDelay(properties.getGrowthDelay());
        this.setDuration(properties.getDuration());
        this.setHeightInitial(properties.getHeightInitial());
        this.setWidthInitial(properties.getWidthInitial());
        this.setHeightFinal(properties.getHeightFinal());
        this.setWidthFinal(properties.getWidthFinal());
        this.setTeamSelector(properties.getTeamSelector());
        this.setBehavior(properties.getBehaviors());
        for (Affected effect : properties.getEffects()) {
            this.setEffect(effect);
        }
    }

    public Map<UUID, Integer> getVictims() {
        return victims;
    }

    public int getDuration() {
        return this.getEntityData().get(DATA_DURATION);
    }

    public void setDuration(int duration) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_DURATION, Mth.clamp(duration, 0, 20));
        }
    }

    public int getGrowthDelay() {
        return this.getEntityData().get(DATA_GROWTH_DELAY);
    }

    public void setGrowthDelay(int waitTime) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_GROWTH_DELAY, waitTime);
        }
    }

    public List<Affected> getEffects() {
        return effects;
    }

    public void setEffect(Affected affected) {
        this.effects.add(affected);
    }

    public float getCurrentWidth() {
        return this.getEntityData().get(DATA_WIDTH);
    }

    public void setCurrentWidth(float pWidth) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_WIDTH, Mth.clamp(pWidth, 0.0F, 32.0F));
        }
    }

    public float getCurrentHeight() {
        return this.getEntityData().get(DATA_HEIGHT);
    }

    public void setCurrentHeight(float pHeight) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_HEIGHT, Mth.clamp(pHeight, 0.0F, 32.0F));
        }
    }

    public float getWidthInitial() {
        return this.widthInitial;
    }

    public void setWidthInitial(float pWidth) {
        this.widthInitial = pWidth;
    }

    public float getHeightInitial() {
        return this.heightInitial;
    }

    public void setHeightInitial(float pHeight) {
        this.heightInitial = pHeight;
    }

    public float getWidthFinal() {
        return this.widthFinal;
    }

    public void setWidthFinal(float pWidth) {
        this.widthFinal = pWidth;
    }

    public float getHeightFinal() {
        return this.heightFinal;
    }

    public void setHeightFinal(float pHeight) {
        this.heightFinal = pHeight;
    }

    public TeamHelper.TeamSelector getTeamSelector() {
        return teamSelector;
    }

    public void setTeamSelector(TeamHelper.TeamSelector teamSelector) {
        this.teamSelector = teamSelector;
    }

    public Set<EntityBehavior<? extends Strategic>> getBehaviors() {
        return this.getEntityData().get(DATA_BEHAVIORS);
    }

    public void setBehavior(Set<EntityBehavior<? extends Strategic>> behavior) {
        this.getEntityData().set(DATA_BEHAVIORS, behavior);
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
        this.setGrowthDelay(pCompound.getInt("GrowthDelay"));
        this.setWidthInitial(pCompound.getFloat("Width"));
        this.setHeightInitial(pCompound.getFloat("Height"));
        this.setCurrentWidth(pCompound.getFloat("mWidth"));
        this.setCurrentHeight(pCompound.getFloat("mHeight"));
        this.setTeamSelector(NBTHelper.getEnum(TeamHelper.TeamSelector.class, pCompound, "TeamSelector"));
        if (pCompound.contains("Strategies", 9)) {
            ListTag listtag = pCompound.getList("Strategies", Tag.TAG_COMPOUND);
            Set<EntityBehavior<? extends Strategic>> array = Sets.newHashSet();

            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag tag = listtag.getCompound(i);
                array.add((EntityBehavior<? extends Strategic>) EntityBehaviorInstances.ENTRIES.get(tag.getInt("Strategy")));
            }

            setBehavior(array);
        }

        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }

        if (pCompound.contains("Effects", 9)) {
            ListTag listtag = pCompound.getList("Effects", Tag.TAG_COMPOUND);
            this.effects.clear();

            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag tag = listtag.getCompound(i);
                Affected affected = new Affected();
                affected.id = tag.getString("Id");
                affected.behavior = GSLRegistries.BEHAVIOR_TYPES.getValue(new ResourceLocation(tag.getString("Resource"))).create();
                affected.behavior.deserializeNBT(tag.getCompound("Behavior"));
                affected.behaviorContext = new BehaviorContext();
                affected.behaviorContext.deserializeNBT(tag.getCompound("BehaviorContext"));
                this.setEffect(affected);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Age", this.tickCount);
        pCompound.putInt("Duration", this.getDuration());
        pCompound.putInt("GrowthDelay", this.getGrowthDelay());
        pCompound.putFloat("Width", this.getWidthInitial());
        pCompound.putFloat("Height", this.getHeightInitial());
        pCompound.putFloat("mWidth", this.getCurrentWidth());
        pCompound.putFloat("mHeight", this.getCurrentHeight());
        NBTHelper.setEnum(pCompound, "TeamSelector", this.getTeamSelector());

        if (!getBehaviors().isEmpty()) {
            ListTag listtag = new ListTag();
            for (EntityBehavior<?> strategy : getBehaviors()) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("Id", strategy.getId());
                listtag.add(tag);
            }
            pCompound.put("Strategies", listtag);
        }

        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }

        if (!this.effects.isEmpty()) {
            ListTag listtag = new ListTag();

            for (Affected affected : this.effects) {
                CompoundTag tag = new CompoundTag();
                tag.putString("Id", affected.id);
                tag.putString("Resource", affected.behavior.getType().getRegistryName().toString());
                tag.put("Behavior", affected.behavior.serializeNBT());
                tag.put("BehaviorContext", affected.behaviorContext.serializeNBT());
                listtag.add(tag);
            }

            pCompound.put("Effects", listtag);
        }
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
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
        return StrategicDimensions.scalable(StrategicDimensions.Type.CENTER, this.getCurrentWidth(), this.getCurrentHeight());
    }
}
