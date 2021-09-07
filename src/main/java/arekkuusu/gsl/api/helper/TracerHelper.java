package arekkuusu.gsl.api.helper;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({"Guava", "ConstantConditions"})
public final class TracerHelper {

    public static Function<Entity, Vec3> TO_EYE_VEC = (e) -> Objects.requireNonNull(e).getEyePosition(1F);
    public static Function<Entity, Vec3> TO_LOOK_VEC = (e) -> Objects.requireNonNull(e).getLookAngle();

    public static HitResult getLookedAt(Entity source, double distance, Predicate<Entity> predicate) {
        Vec3 from = TO_EYE_VEC.apply(source);
        Vec3 to = from.add(TO_LOOK_VEC.apply(source).multiply(distance, distance, distance));
        HitResult result = getLookedBlock(source, from, to);
        if(result.getType() != HitResult.Type.MISS) {
            to = result.getLocation();
        }
        HitResult result1 = getLookedEntity(source, from, to, predicate);
        return result1.getType() == HitResult.Type.MISS ? result : result1;
    }

    public static BlockHitResult getLookedBlock(Entity source, Vec3 start, Vec3 end) {
        Level world = source.level;
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source);
        return world.clip(context);
    }

    public static EntityHitResult getLookedEntity(Entity source, Vec3 start, Vec3 end, Predicate<Entity> predicate) {
        Level world = source.level;
        Vec3 difference = end.subtract(start);
        double distance = start.distanceTo(end);
        AABB bb = new AABB(new BlockPos(start))
                .expandTowards(difference.multiply(distance, distance, distance))
                .inflate(1.0D);
        EntityHitResult result = TracerHelper.rayTraceEntities(world, source, start, end, bb, predicate);
        if (result == null) {
            result = new EntityRayTraceResultEmpty();
        }
        return result;
    }

    public static List<Entity> getInCone(Entity source, double distance, double degrees, Predicate<Entity> predicate) {
        Vec3 eyesVector = source.getEyePosition(1F);
        Vec3 lookVector = source.getLookAngle();
        Vec3 targetVector = eyesVector.add(
                lookVector.x * distance,
                lookVector.y * distance,
                lookVector.z * distance
        );
        AABB bb = new AABB(
                targetVector.x - distance, targetVector.y - distance, targetVector.z - distance,
                targetVector.x + distance, targetVector.y + distance, targetVector.z + distance
        );
        List<Entity> entities = Lists.newArrayList();
        for (Entity entity : source.level.getEntities(source, bb, predicate)) {
            if (isInCone(entity, source, degrees)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    public static boolean isInCone(Entity source, Entity target, double fov) {
        Vec3 positionTarget = target.getEyePosition(1F);
        Vec3 lookTarget = target.getLookAngle().normalize();
        Vec3 positionAttacker = source.getEyePosition(1F);

        Vec3 origin = new Vec3(0, 0, 0);
        Vec3 pointA = lookTarget.add(positionTarget).subtract(positionTarget);
        Vec3 pointB = positionAttacker.subtract(positionTarget);
        double pointADistance = pointA.distanceTo(pointB);
        double pointBDistance = pointB.distanceTo(origin);

        if (pointADistance < pointBDistance) {
            double ab = (pointA.x * pointB.x) + (pointA.y * pointB.y) + (pointA.z * pointB.z);
            double a = Math.sqrt(Math.pow(pointA.x, 2D) + Math.pow(pointA.y, 2D) + Math.pow(pointA.z, 2D));
            double b = Math.sqrt(Math.pow(pointB.x, 2D) + Math.pow(pointB.y, 2D) + Math.pow(pointB.z, 2D));
            double angle = Math.acos(ab / (a * b)) * (180 / Math.PI);
            return angle > -fov && angle < fov;
        }
        return false;
    }

    @Nullable
    public static EntityHitResult rayTraceEntities(Level worldIn, Entity projectile, Vec3 startVec, Vec3 endVec, AABB boundingBox, java.util.function.Predicate<Entity> filter) {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;
        Vec3 entityVector = null;

        for(Entity entity1 : worldIn.getEntities(projectile, boundingBox, filter)) {
            AABB axisalignedbb = entity1.getBoundingBox().inflate(0.3D);
            Optional<Vec3> optional = axisalignedbb.clip(startVec, endVec);
            if (optional.isPresent()) {
                double d1 = startVec.distanceToSqr(optional.get());
                if (d1 < d0) {
                    entity = entity1;
                    entityVector = optional.get();
                    d0 = d1;
                }
            }
        }

        return entity == null || entityVector == null ? null : new EntityHitResult(entity, entityVector);
    }

    public static final class EntityRayTraceResultEmpty extends EntityHitResult {

        EntityRayTraceResultEmpty() {
            super(null, null);
        }

        @Override
        @Nonnull
        public Type getType() {
            return Type.MISS;
        }
    }
}