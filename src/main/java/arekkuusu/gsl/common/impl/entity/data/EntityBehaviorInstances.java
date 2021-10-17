package arekkuusu.gsl.common.impl.entity.data;

import arekkuusu.gsl.api.helper.GSLHelper;
import arekkuusu.gsl.api.helper.TeamHelper;
import arekkuusu.gsl.common.impl.entity.Strategic;
import arekkuusu.gsl.common.impl.entity.StrategicBlocks;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EntityBehaviorInstances {
    public static final Int2ObjectMap<EntityBehavior<? extends Entity>> ENTRIES = new Int2ObjectOpenHashMap<>();
    public static final int TIME_BETWEEN_APPLICATIONS = 5;
    public static int id = 0;

    public static final EntityBehavior<Strategic> NO_IMPLEMENT = new EntityBehavior<>(id++) {

        @Override
        public void tick(Strategic strategic) {
        }
    };

    public static final EntityBehavior<Strategic> GROW_EVENLY = new EntityBehavior<>(id++) {

        @Override
        public void tick(Strategic strategic) {
            if (strategic.level.isClientSide())
                return;
            float currentHeight = strategic.getCurrentHeight();
            float currentWidth = strategic.getCurrentWidth();
            float heightInitial = strategic.getHeightInitial();
            float widthInitial = strategic.getWidthInitial();
            float heightFinal = strategic.getHeightFinal();
            float widthFinal = strategic.getWidthFinal();
            int growthDelay = strategic.getGrowthDelay();
            if (strategic.tickCount < growthDelay) {
                float heightPerTick = (heightFinal - heightInitial) / growthDelay;
                float widthPerTick = (widthFinal - widthInitial) / growthDelay;
                strategic.setCurrentHeight(Mth.clamp(currentHeight + heightPerTick, heightInitial, heightFinal));
                strategic.setCurrentWidth(Mth.clamp(currentWidth + widthPerTick, widthInitial, widthFinal));
            }
        }
    };

    public static final EntityBehavior<Strategic> SCAN_UNIQUE = new EntityBehavior<>(id++) {

        @Override
        public void tick(Strategic strategic) {
            if (strategic.tickCount % TIME_BETWEEN_APPLICATIONS != 0)
                return;
            if (strategic.level.isClientSide())
                return;

            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if (!victims.containsKey(user.getUUID())) {
                    victims.put(user.getUUID(), 0);
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }
    };

    public static final EntityBehavior<Strategic> SCAN_ALWAYS = new EntityBehavior<>(id++) {

        @Override
        public void tick(Strategic strategic) {
            if (strategic.tickCount % TIME_BETWEEN_APPLICATIONS != 0)
                return;
            if (strategic.level.isClientSide())
                return;

            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                Integer integer = victims.putIfAbsent(user.getUUID(), 0);
                if (victims.replace(user.getUUID(), integer, Objects.nonNull(integer) ? ++integer : 0)) {
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }
    };

    public static final EntityBehavior<StrategicBlocks> SPREAD_FLOOR_BLOCKS = new EntityBehavior<StrategicBlocks>(id++) {

        boolean isSpread;

        @Override
        public void tick(StrategicBlocks strategic) {
            if (!this.isSpread) {
                if (strategic.getBlocks().length == 0) {
                    this.spreadOnTerrain(strategic);
                }
                this.isSpread = true;
            }
        }

        public void spreadOnTerrain(StrategicBlocks strategic) {
            BlockPos[][] positions = strategic.getBlocks();
            int radius = (int) Math.floor(strategic.getWidthInitial());
            Set<BlockPos> visited = new HashSet<>();
            Set<BlockPos> queue = new HashSet<>();
            BlockPos origin = strategic.getOnPos().below(); //Down into the ground if any
            BlockPos original = getValid(strategic.level, origin); //Look for the ground
            if (original != null) {
                positions = Arrays.copyOf(positions, positions.length + 1);
                positions[0] = new BlockPos[]{original};
                visited.add(original); //We know our original position is valid
                addNext(queue, original); //Add next on queue
                int i = 1;
                while (true) {
                    positions = Arrays.copyOf(positions, positions.length + 1);
                    positions[i] = new BlockPos[0];
                    Set<BlockPos> temp = new HashSet<>();
                    int j = 0;
                    for (BlockPos pos : queue) {
                        BlockPos validated = getValid(strategic.level, pos);
                        if (validated != null && isWithingRadius(original, validated, radius)) {
                            if (visited.add(validated)) {
                                positions[i] = Arrays.copyOf(positions[i], positions[i].length + 1);
                                positions[i][j] = validated;
                                addNext(temp, validated);
                                j++;
                            }
                        }
                    }
                    if (temp.isEmpty()) break;
                    queue = temp;
                    i++;
                }
            }
            strategic.setBlocks(positions);
        }

        @Nullable
        public BlockPos getValid(Level level, BlockPos pos) {
            BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

            if (!isSolid(level, mPos)) {
                for (int j = 0; ; j++) {
                    if (j == 1) return null;
                    mPos.move(Direction.DOWN);
                    if (isSolid(level, mPos)) {
                        return mPos.immutable();
                    }
                }
            } else if (isSolid(level, mPos.above())) {
                for (int j = 0; ; j++) {
                    if (j == 1) return null;
                    mPos.move(Direction.UP);
                    if (!isSolid(level, mPos.above())) {
                        return mPos.immutable();
                    }
                }
            }
            return mPos.immutable();
        }

        public boolean isWithingRadius(BlockPos origin, BlockPos pos, int distance) {
            double x = (origin.getX() + 0.5D) - (pos.getX() + 0.5D);
            double y = (origin.getY() + 0.5D) - (pos.getY() + 0.5D);
            double z = (origin.getZ() + 0.5D) - (pos.getZ() + 0.5D);
            return Math.sqrt(x * x + y * y + z * z) < distance;
        }

        public boolean isSolid(Level level, BlockPos pos) {
            BlockState state = level.getBlockState(pos);
            return state.getCollisionShape(level, pos) != Shapes.empty();
        }

        public void addNext(Set<BlockPos> list, BlockPos origin) {
            list.add(origin.relative(Direction.NORTH));
            list.add(origin.relative(Direction.SOUTH));
            list.add(origin.relative(Direction.EAST));
            list.add(origin.relative(Direction.WEST));
        }
    };

    public static final EntityBehavior<StrategicBlocks> SCAN_BLOCKS_UNIQUE = new EntityBehavior<>(id++) {

        @Override
        public void tick(StrategicBlocks strategic) {
            if (strategic.tickCount % TIME_BETWEEN_APPLICATIONS != 0)
                return;
            if (strategic.level.isClientSide())
                return;

            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            var blocks = strategic.getBlocksWithCursor();
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                boolean withinHeight = false;
                BlockPos entityPos = user.getOnPos();
                BlockPos pos = entityPos.below();
                if (Arrays.stream(blocks).anyMatch(l -> Arrays.asList(l).contains(pos))) {
                    withinHeight = pos.getY() - user.getY() <= 1;
                }
                if (!victims.containsKey(user.getUUID()) && withinHeight) {
                    victims.put(user.getUUID(), 0);
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }
    };

    public static final EntityBehavior<StrategicBlocks> SCAN_BLOCKS_ALWAYS = new EntityBehavior<>(id++) {

        @Override
        public void tick(StrategicBlocks strategic) {
            if (strategic.tickCount % TIME_BETWEEN_APPLICATIONS != 0)
                return;
            if (strategic.level.isClientSide())
                return;

            var victims = strategic.getVictims();
            var world = strategic.level;
            var owner = strategic.getOwner();
            var effects = strategic.getEffects();
            var team = strategic.getTeamSelector().apply(owner);
            var blocks = strategic.getBlocksWithCursor();
            world.getEntities(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                boolean withinHeight = false;
                BlockPos entityPos = user.getOnPos();
                BlockPos pos = entityPos.below();
                if (Arrays.stream(blocks).anyMatch(l -> Arrays.asList(l).contains(pos))) {
                    withinHeight = pos.getY() - user.getY() <= 1;
                }
                if (withinHeight) {
                    Integer integer = victims.putIfAbsent(user.getUUID(), 0);
                    if (victims.replace(user.getUUID(), integer, Objects.nonNull(integer) ? ++integer : 0)) {
                        effects.forEach(affected -> {
                            GSLHelper.applyEffectOn(user, affected);
                        });
                    }
                }
            });
        }
    };

    static {
        ENTRIES.put(NO_IMPLEMENT.getId(), NO_IMPLEMENT);
        ENTRIES.put(GROW_EVENLY.getId(), GROW_EVENLY);
        ENTRIES.put(SCAN_UNIQUE.getId(), SCAN_UNIQUE);
        ENTRIES.put(SCAN_ALWAYS.getId(), SCAN_ALWAYS);
        ENTRIES.put(SPREAD_FLOOR_BLOCKS.getId(), SPREAD_FLOOR_BLOCKS);
        ENTRIES.put(SCAN_BLOCKS_UNIQUE.getId(), SCAN_BLOCKS_UNIQUE);
        ENTRIES.put(SCAN_BLOCKS_ALWAYS.getId(), SCAN_BLOCKS_ALWAYS);
    }
}
