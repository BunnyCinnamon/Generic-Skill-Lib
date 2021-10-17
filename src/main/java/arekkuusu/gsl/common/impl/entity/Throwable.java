package arekkuusu.gsl.common.impl.entity;

import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.common.impl.DefaultEntities;
import arekkuusu.gsl.common.impl.entity.data.EntityProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class Throwable extends AbstractHurtingProjectile {

    private EntityProperties<?> entityProperties = new EntityProperties<>();
    private EntityType<?> entityType = DefaultEntities.STRATEGIC.get();
    private float width = 0.5F, height = 0.5F;
    private int flyTime;

    public Throwable(EntityType<? extends Throwable> p_37199_, Level p_37200_) {
        super(p_37199_, p_37200_);
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            super.tick();

            if(this.tickCount > getFlyTime()) {
                HitResult hitResult = new HitResult(getPosition(1F)) {
                    @Override
                    public Type getType() {
                        return Type.MISS;
                    }
                };
                this.onHit(hitResult);
            }

            HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }

            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float f = this.getInertia();
            if (this.isInWater()) {
                for(int i = 0; i < 4; ++i) {
                    float f1 = 0.25F;
                    this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
                }

                f = 0.8F;
            }

            this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale((double)f));
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
                this.level.addFreshEntity(instance);
            }
            this.discard();
        }
    }

    public EntityProperties<?> getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties<?> entityProperties) {
        this.entityProperties = entityProperties;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType<?> entityType) {
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
        this.setEntityType(GSLRegistries.ENTITY_TYPES.getValue(new ResourceLocation(pCompound.getString("EntityType"))));
        EntityProperties<Strategic> properties = new EntityProperties<>();
        properties.readAdditionalSaveData(pCompound.getCompound("Properties"));
        this.setEntityProperties(properties);
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
    }
}
