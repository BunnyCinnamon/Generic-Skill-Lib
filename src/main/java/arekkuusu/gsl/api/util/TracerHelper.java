package arekkuusu.gsl.api.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({"Guava", "ConstantConditions"})
public final class TracerHelper {

    public static Function<Entity, Vector3d> TO_EYE_VEC = (e) -> Objects.requireNonNull(e).getEyePosition(1F);
    public static Function<Entity, Vector3d> TO_LOOK_VEC = (e) -> Objects.requireNonNull(e).getLook(1F);

    public static RayTraceResult getLookedAt(Entity source, double distance, Predicate<Entity> predicate) {
        Vector3d from = TO_EYE_VEC.apply(source);
        Vector3d to = from.add(TO_LOOK_VEC.apply(source).mul(distance, distance, distance));
        RayTraceResult result = getLookedBlock(source, from, to);
        if(result.getType() != RayTraceResult.Type.MISS) {
            to = result.getHitVec();
        }
        RayTraceResult result1 = getLookedEntity(source, from, to, predicate);
        return result1.getType() == RayTraceResult.Type.MISS ? result : result1;
    }

    public static BlockRayTraceResult getLookedBlock(Entity source, Vector3d start, Vector3d end) {
        World world = source.getEntityWorld();
        RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, source);
        return world.rayTraceBlocks(context);
    }

    public static EntityRayTraceResult getLookedEntity(Entity source, Vector3d start, Vector3d end, Predicate<Entity> predicate) {
        World world = source.getEntityWorld();
        Vector3d difference = end.subtract(start);
        double distance = start.distanceTo(end);
        AxisAlignedBB bb = new AxisAlignedBB(new BlockPos(start))
                .expand(difference.mul(distance, distance, distance))
                .grow(1.0D);
        EntityRayTraceResult result = TracerHelper.rayTraceEntities(world, source, start, end, bb, predicate);
        if (result == null) {
            result = new EntityRayTraceResultEmpty();
        }
        return result;
    }

    public static List<Entity> getInCone(Entity source, double distance, double degrees, Predicate<Entity> predicate) {
        Vector3d eyesVector = source.getEyePosition(1F);
        Vector3d lookVector = source.getLook(1F);
        Vector3d targetVector = eyesVector.add(
                lookVector.x * distance,
                lookVector.y * distance,
                lookVector.z * distance
        );
        AxisAlignedBB bb = new AxisAlignedBB(
                targetVector.x - distance, targetVector.y - distance, targetVector.z - distance,
                targetVector.x + distance, targetVector.y + distance, targetVector.z + distance
        );
        List<Entity> entities = Lists.newArrayList();
        for (Entity entity : source.world.getEntitiesInAABBexcluding(source, bb, predicate)) {
            if (isInCone(entity, source, degrees)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    public static boolean isInCone(Entity source, Entity target, double fov) {
        Vector3d positionTarget = target.getEyePosition(1F);
        Vector3d lookTarget = target.getLookVec().normalize();
        Vector3d positionAttacker = source.getEyePosition(1F);

        Vector3d origin = new Vector3d(0, 0, 0);
        Vector3d pointA = lookTarget.add(positionTarget).subtract(positionTarget);
        Vector3d pointB = positionAttacker.subtract(positionTarget);
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
    public static EntityRayTraceResult rayTraceEntities(World worldIn, Entity projectile, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, java.util.function.Predicate<Entity> filter) {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;
        Vector3d entityVector = null;

        for(Entity entity1 : worldIn.getEntitiesInAABBexcluding(projectile, boundingBox, filter)) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)0.3F);
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);
            if (optional.isPresent()) {
                double d1 = startVec.squareDistanceTo(optional.get());
                if (d1 < d0) {
                    entity = entity1;
                    entityVector = optional.get();
                    d0 = d1;
                }
            }
        }

        return entity == null || entityVector == null ? null : new EntityRayTraceResult(entity, entityVector);
    }

    public static final class EntityRayTraceResultEmpty extends EntityRayTraceResult {

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
