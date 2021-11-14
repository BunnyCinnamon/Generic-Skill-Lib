package arekkuusu.gsl.common.impl.entity;

import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.api.helper.TracerHelper;
import arekkuusu.gsl.common.impl.DefaultEntities;
import arekkuusu.gsl.common.impl.entity.data.EntityProperties;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class Throwable extends AbstractHurtingProjectile {

    private static final EntityDataAccessor<Float> DATA_X_ROT = SynchedEntityData.defineId(Throwable.class, net.minecraft.network.syncher.EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Y_ROT = SynchedEntityData.defineId(Throwable.class, net.minecraft.network.syncher.EntityDataSerializers.FLOAT);

    private EntityProperties entityProperties = new EntityProperties();
    private EntityType<?> entityType = DefaultEntities.STRATEGIC.get();
    private float width = 0.5F, height = 0.5F;
    private int flyTime;

    public Throwable(EntityType<? extends Throwable> p_37199_, Level p_37200_) {
        super(p_37199_, p_37200_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_Y_ROT, 0F);
        this.getEntityData().define(DATA_X_ROT, 0F);
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            super.tick();

            if(this.tickCount > getFlyTime()) {
                var hitResult = new TracerHelper.RayTraceResultEmpty(getPosition(1F));
                this.onHit(hitResult);
            }

            var hitResult = TracerHelper.getHitResult(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitResult)) {
                this.onHit(hitResult);
            }

            this.checkInsideBlocks();
            var vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            ProjectileUtil.rotateTowardsMovement(this, 1F);
            this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower));
            this.setPos(d0, d1, d2);

            this.level.addParticle(this.getTrailParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
        } else {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide() && !this.isRemoved()) {
            var owner = (LivingEntity) this.getOwner();
            var instance = (Strategic) entityType.create(this.level);
            if(Objects.nonNull(instance)) {
                instance.applyProperties(entityProperties);
                instance.setOwner(owner);
                instance.setPos(pResult.getLocation());
                instance.setXRot(Mth.wrapDegrees(getXRot()));
                instance.setYRot(Mth.wrapDegrees(getYRot() + 180F));

                if(pResult instanceof BlockHitResult) {
                    instance.setDirection(((BlockHitResult) pResult).getDirection());
                } else {
                    instance.setDirection(null);
                }

                if(instance.getDimensionsType() == StrategicDimensions.Type.ON_HIT) {
                    if(instance.getDirection() != null) {
                        instance.setDimensionsType(switch (instance.getDirection()) {
                            case UP -> StrategicDimensions.Type.UP;
                            case NORTH -> StrategicDimensions.Type.NORTH;
                            case SOUTH -> StrategicDimensions.Type.SOUTH;
                            case WEST -> StrategicDimensions.Type.WEST;
                            case EAST -> StrategicDimensions.Type.EAST;
                            default -> StrategicDimensions.Type.DOWN;
                        });

                        var normal = instance.getDirection().getNormal();
                        instance.lookAt(EntityAnchorArgument.Anchor.FEET, instance.getEyePosition().add(normal.getX(), normal.getY(), normal.getZ()));
                    } else {
                        instance.setDimensionsType(StrategicDimensions.Type.CENTER);
                    }
                }
                this.level.addFreshEntity(instance);
            }
            this.discard();
        }
    }

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntitySpawnProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public void setEntitySpawn(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public int getFlyTime() {
        return flyTime;
    }

    public void setFlyTime(int flyTime) {
        this.flyTime = flyTime;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float pWidth) {
        this.width = pWidth;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float pHeight) {
        this.height = pHeight;
    }

    public float getYRot() {
        return this.getEntityData().get(DATA_Y_ROT);
    }

    public void setYRot(float p_146923_) {
        if (!Float.isFinite(p_146923_)) {
            Util.logAndPauseIfInIde("Invalid entity rotation: " + p_146923_ + ", discarding.");
        } else {
            this.getEntityData().set(DATA_Y_ROT, p_146923_);
        }
    }

    public float getXRot() {
        return this.getEntityData().get(DATA_X_ROT);
    }

    public void setXRot(float p_146927_) {
        if (!Float.isFinite(p_146927_)) {
            Util.logAndPauseIfInIde("Invalid entity rotation: " + p_146927_ + ", discarding.");
        } else {
            this.getEntityData().set(DATA_X_ROT, p_146927_);
        }
    }

    public void setOwnerDirection(LivingEntity owner, Vec3 target) {
        Vec3 vec3 = owner.getViewVector(1.0F);
        double d2 = target.x() - (owner.getX() + vec3.x * 0.5D);
        double d3 = target.y() - (owner.getEyeY() + vec3.y * 0.5D);
        double d4 = target.z() - (owner.getZ() + vec3.z * 0.5D);
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
        this.setPos(owner.getX() + vec3.x * 0.5D, owner.getEyeY() + vec3.y * 0.5D, owner.getZ() + vec3.z * 0.5D);
    }

    public void setFlySpeed(double speed) {
        this.xPower *= speed;
        this.yPower *= speed;
        this.zPower *= speed;
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
    public EntityDimensions getDimensions(Pose pPose) {
        return StrategicDimensions.scalable(StrategicDimensions.Type.CENTER, this.width, this.height);
    }

    @Override
    protected boolean canHitEntity(Entity p_36842_) {
        return super.canHitEntity(p_36842_) && this.entityProperties.getTeamSelector().apply(getOwner()).test(p_36842_);
    }

    @Override
    public boolean isInvulnerable() {
        return true;
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
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ELECTRIC_SPARK;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setWidth(pCompound.getFloat("Width"));
        this.setHeight(pCompound.getFloat("Height"));
        this.setFlyTime(pCompound.getInt("FlyTime"));
        this.setEntitySpawn(GSLRegistries.ENTITY_TYPES.getValue(new ResourceLocation(pCompound.getString("EntityType"))));
        EntityProperties properties = new EntityProperties();
        properties.readAdditionalSaveData(pCompound.getCompound("Properties"));
        this.setEntitySpawnProperties(properties);
        this.setXRot(pCompound.getFloat("XRot"));
        this.setYRot(pCompound.getFloat("YRot"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Width", this.getWidth());
        pCompound.putFloat("Height", this.getHeight());
        pCompound.putFloat("FlyTime", this.getFlyTime());
        pCompound.putString("EntityType", this.getEntityType().getRegistryName().toString());
        CompoundTag compoundTag = new CompoundTag();
        this.getEntityProperties().addAdditionalSaveData(compoundTag);
        pCompound.put("Properties", compoundTag);
        pCompound.putFloat("XRot", getXRot());
        pCompound.putFloat("YRot", getYRot());
    }
}
