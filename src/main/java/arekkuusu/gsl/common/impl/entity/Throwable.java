package arekkuusu.gsl.common.impl.entity;

import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.capability.data.Affected;
import arekkuusu.gsl.api.helper.NBTHelper;
import arekkuusu.gsl.api.helper.TeamHelper;
import arekkuusu.gsl.api.registry.data.BehaviorContext;
import arekkuusu.gsl.common.impl.DefaultEntities;
import arekkuusu.gsl.common.impl.entity.data.GSLDataSerializers;
import arekkuusu.gsl.common.impl.entity.data.GSLStrategyInstances;
import arekkuusu.gsl.common.impl.entity.data.Strategy;
import com.google.common.collect.Lists;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Throwable extends AbstractHurtingProjectile {

    private static final EntityDataAccessor<Strategy<Strategic>[]> DATA_STRATEGY = SynchedEntityData.defineId(Throwable.class, GSLDataSerializers.STRATEGY);
    private static final EntityDataAccessor<EntityType<?>> DATA_ENTITY = SynchedEntityData.defineId(Throwable.class, GSLDataSerializers.ENTITY_TYPE);

    private final List<Affected> effects = Lists.newArrayList();
    private TeamHelper.TeamSelector teamSelector = TeamHelper.TeamSelector.ANY;
    private float width, height;

    public Throwable(EntityType<? extends Throwable> p_37199_, Level p_37200_) {
        super(p_37199_, p_37200_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_STRATEGY, (Strategy<Strategic>[]) new Strategy<?>[]{GSLStrategyInstances.NO_IMPLEMENT});
        this.getEntityData().define(DATA_ENTITY, DefaultEntities.STRATEGIC.get());
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide()) {
            var owner = (LivingEntity) this.getOwner();
            var entity = this.getEntityData().get(DATA_ENTITY);
            var strategy = this.getEntityData().get(DATA_STRATEGY);
            var instance = (Strategic) entity.create(this.level);
            if(Objects.nonNull(instance)) {
                instance.addAllEffect(effects);
                instance.setTeamSelector(getTeamSelector());
                instance.setStrategy(strategy);
                instance.setMaxWidth(getWidth());
                instance.setMaxHeight(getHeight());
                instance.setOwner(owner);
                instance.setPos(this.getPosition(1F));
                this.level.addFreshEntity(instance);
            }
            this.discard();
        }
    }

    public void setTeamSelector(TeamHelper.TeamSelector teamSelector) {
        this.teamSelector = teamSelector;
    }

    public void setStrategy(Strategy<Strategic>... strategy) {
        if (!this.level.isClientSide()) {
            this.getEntityData().set(DATA_STRATEGY, strategy);
        }
    }

    public void setWidth(float pWidth) {
        this.width = pWidth;
    }

    public void setHeight(float pHeight) {
        this.height = pHeight;
    }

    public TeamHelper.TeamSelector getTeamSelector() {
        return teamSelector;
    }

    public Strategy<Strategic>[] getStrategy() {
        return this.getEntityData().get(DATA_STRATEGY);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setOwnerDirection(LivingEntity owner, Vec3 target) {
        Vec3 vec3 = owner.getViewVector(1.0F);
        double d2 = target.x() - (owner.getX() + vec3.x * 2.0D);
        double d3 = target.y() - (owner.getY(0.5D) + vec3.y * 2.0D);
        double d4 = target.z() - (owner.getZ() + vec3.z * 2.0D);
        this.setOwner(owner);
        this.setRot(owner.getYRot(), owner.getXRot());
        this.moveTo(d2, d3, d4, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double d0 = Math.sqrt(d2 * d2 + d3 * d3 + d4 * d4);
        if (d0 != 0.0D) {
            this.xPower = d2 / d0 * 0.1D;
            this.yPower = d3 / d0 * 0.1D;
            this.zPower = d4 / d0 * 0.1D;
        }
        this.setPos(owner.getX() + vec3.x * 2.0D, owner.getY(0.5D) + vec3.y * 2.0D, owner.getZ() + vec3.z * 2.0D);
    }

    public void addEffect(Affected affected) {
        this.effects.add(affected);
    }

    public void addAllEffect(Collection<Affected> affected) {
        this.effects.addAll(affected);
    }

    @Override
    protected boolean canHitEntity(Entity p_36842_) {
        return super.canHitEntity(p_36842_) && this.getTeamSelector().apply(getOwner()).test(p_36842_);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ELECTRIC_SPARK;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setTeamSelector(NBTHelper.getEnum(TeamHelper.TeamSelector.class, pCompound, "TeamSelector"));
        this.setWidth(pCompound.getFloat("Width"));
        this.setHeight(pCompound.getFloat("Height"));
        this.setStrategy(GSLStrategyInstances.ENTRIES.get(pCompound.getInt("Strategy")));
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
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        NBTHelper.setEnum(pCompound, "TeamSelector", this.getTeamSelector());
        pCompound.putFloat("Width", this.getWidth());
        pCompound.putFloat("Height", this.getHeight());
        pCompound.putInt("Strategy", this.getStrategy().getId());
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
}
