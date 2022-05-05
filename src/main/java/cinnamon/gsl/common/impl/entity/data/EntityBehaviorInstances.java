package cinnamon.gsl.common.impl.entity.data;

import cinnamon.gsl.api.helper.GSLHelper;
import cinnamon.gsl.api.helper.TeamHelper;
import cinnamon.gsl.api.helper.TracerHelper;
import cinnamon.gsl.common.impl.entity.Strategic;
import cinnamon.gsl.common.impl.entity.StrategicBlocks;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EntityBehaviorInstances {
    public static final Int2ObjectMap<EntityBehavior<? extends Entity>> ENTRIES = new Int2ObjectOpenHashMap<>();
    public static final int TIME_BETWEEN_APPLICATIONS = 5;
    public static int ID_INCREMENT = 0;

    public static final EntityBehavior<Strategic> NO_IMPLEMENT = new EntityBehavior<>(ID_INCREMENT++) {

        @Override
        public void tick(Strategic strategic) {
        }
    };

    public static final EntityBehavior<Strategic> EXPAND_EVENLY = new EntityBehavior<>(ID_INCREMENT++) {

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

    public static final EntityBehavior<Strategic> EXPAND_HEIGHT_FIRST = new EntityBehavior<>(ID_INCREMENT++) {

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
                float widthPerTick = (widthFinal - widthInitial) / growthDelay;
                strategic.setCurrentWidth(Mth.clamp(currentWidth + widthPerTick, widthInitial, widthFinal));
            }
            if (strategic.tickCount < growthDelay / 2) {
                float heightPerTick = (heightFinal - heightInitial) / (growthDelay / 2F);
                strategic.setCurrentHeight(Mth.clamp(currentHeight + heightPerTick, heightInitial, heightFinal));
            }
        }
    };

    public static final EntityBehavior<Strategic> EXPAND_WIDTH_FIRST = new EntityBehavior<>(ID_INCREMENT++) {

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
                strategic.setCurrentHeight(Mth.clamp(currentHeight + heightPerTick, heightInitial, heightFinal));
            }
            if (strategic.tickCount < growthDelay / 2) {
                float widthPerTick = (widthFinal - widthInitial) / (growthDelay / 2F);
                strategic.setCurrentWidth(Mth.clamp(currentWidth + widthPerTick, widthInitial, widthFinal));
            }
        }
    };

    public static final EntityBehavior<StrategicBlocks> EXPAND_FLOOR_BLOCKS = new EntityBehavior<StrategicBlocks>(ID_INCREMENT++) {

        @Override
        public void tick(StrategicBlocks strategic) {
            if (!strategic.level.isClientSide() && strategic.getBlocks().length == 0) {
                this.spreadOnTerrain(strategic);
            }
        }

        public void spreadOnTerrain(StrategicBlocks strategic) {
            var positions = strategic.getBlocks();
            int radius = (int) Math.floor(strategic.getWidthFinal());
            var visited = new HashSet<BlockPos>();
            var queue = new HashSet<BlockPos>();
            var origin = strategic.getOnPos().below(); //Down into the ground if any
            var original = getValid(strategic.level, origin); //Look for the ground
            if (original != null) {
                positions = Arrays.copyOf(positions, positions.length + 1);
                positions[0] = new BlockPos[]{original};
                visited.add(original); //We know our original position is valid
                addNext(queue, original); //Add next on queue
                int i = 1;
                while (true) {
                    positions = Arrays.copyOf(positions, positions.length + 1);
                    positions[i] = new BlockPos[0];
                    var temp = new HashSet<BlockPos>();
                    int j = 0;
                    for (var pos : queue) {
                        var validated = getValid(strategic.level, pos);
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
            var mPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

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
            var state = level.getBlockState(pos);
            return state.getCollisionShape(level, pos) != Shapes.empty();
        }

        public void addNext(Set<BlockPos> list, BlockPos origin) {
            list.add(origin.relative(Direction.NORTH));
            list.add(origin.relative(Direction.SOUTH));
            list.add(origin.relative(Direction.EAST));
            list.add(origin.relative(Direction.WEST));
        }
    };

    public static final EntityBehavior<Strategic> SCAN_UNIQUE_BB = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if (!victims.containsKey(user.getUUID())) {
                    victims.put(user.getUUID(), 0);
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }
    };

    public static final EntityBehavior<Strategic> SCAN_ALWAYS_BB = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                Integer integer = victims.putIfAbsent(user.getUUID(), 0);
                if (victims.replace(user.getUUID(), integer, Objects.nonNull(integer) ? ++integer : 0)) {
                    effects.forEach(affected -> {
                        GSLHelper.applyEffectOn(user, affected);
                    });
                }
            });
        }
    };

    public static final EntityBehavior<Strategic> SCAN_UNIQUE_CONE_20 = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(TracerHelper.isInCone(strategic, user, 20)) {
                    if (!victims.containsKey(user.getUUID())) {
                        victims.put(user.getUUID(), 0);
                        effects.forEach(affected -> {
                            GSLHelper.applyEffectOn(user, affected);
                        });
                    }
                }
            });
        }
    };

    public static final EntityBehavior<Strategic> SCAN_UNIQUE_CONE_40 = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(TracerHelper.isInCone(strategic, user, 40)) {
                    if (!victims.containsKey(user.getUUID())) {
                        victims.put(user.getUUID(), 0);
                        effects.forEach(affected -> {
                            GSLHelper.applyEffectOn(user, affected);
                        });
                    }
                }
            });
        }
    };

    public static final EntityBehavior<Strategic> SCAN_UNIQUE_CONE_60 = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(TracerHelper.isInCone(strategic, user, 60)) {
                    if (!victims.containsKey(user.getUUID())) {
                        victims.put(user.getUUID(), 0);
                        effects.forEach(affected -> {
                            GSLHelper.applyEffectOn(user, affected);
                        });
                    }
                }
            });
        }
    };

    public static final EntityBehavior<Strategic> SCAN_UNIQUE_CONE_80 = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(TracerHelper.isInCone(strategic, user, 80)) {
                    if (!victims.containsKey(user.getUUID())) {
                        victims.put(user.getUUID(), 0);
                        effects.forEach(affected -> {
                            GSLHelper.applyEffectOn(user, affected);
                        });
                    }
                }
            });
        }
    };

    public static final EntityBehavior<Strategic> SCAN_ALWAYS_CONE_20 = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(TracerHelper.isInCone(strategic, user, 20)) {
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

    public static final EntityBehavior<Strategic> SCAN_ALWAYS_CONE_40 = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(TracerHelper.isInCone(strategic, user, 40)) {
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

    public static final EntityBehavior<Strategic> SCAN_ALWAYS_CONE_60 = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(TracerHelper.isInCone(strategic, user, 60)) {
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

    public static final EntityBehavior<Strategic> SCAN_ALWAYS_CONE_80 = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                if(TracerHelper.isInCone(strategic, user, 80)) {
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

    public static final EntityBehavior<StrategicBlocks> SCAN_BLOCKS_UNIQUE = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                boolean withinHeight = false;
                var entityPos = user.getOnPos();
                var pos = entityPos;
                if (Arrays.stream(blocks).filter(Objects::nonNull).anyMatch(l -> Arrays.asList(l).contains(pos))) {
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

    public static final EntityBehavior<StrategicBlocks> SCAN_BLOCKS_ALWAYS = new EntityBehavior<>(ID_INCREMENT++) {

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
            world.getEntitiesOfClass(TeamHelper.typeTest(), strategic.getBoundingBox(), team).forEach(user -> {
                boolean withinHeight = false;
                var entityPos = user.getOnPos();
                var pos = entityPos.below();
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
        put(NO_IMPLEMENT);
        put(EXPAND_EVENLY);
        put(EXPAND_HEIGHT_FIRST);
        put(EXPAND_WIDTH_FIRST);
        put(EXPAND_FLOOR_BLOCKS);
        put(SCAN_UNIQUE_BB);
        put(SCAN_ALWAYS_BB);
        put(SCAN_BLOCKS_UNIQUE);
        put(SCAN_BLOCKS_ALWAYS);
        put(SCAN_ALWAYS_CONE_20);
        put(SCAN_ALWAYS_CONE_40);
        put(SCAN_ALWAYS_CONE_60);
        put(SCAN_ALWAYS_CONE_80);
        put(SCAN_UNIQUE_CONE_20);
        put(SCAN_UNIQUE_CONE_40);
        put(SCAN_UNIQUE_CONE_60);
        put(SCAN_UNIQUE_CONE_80);
    }

    public static void put(EntityBehavior<? extends Entity> behavior) {
        ENTRIES.put(behavior.getId(), behavior);
    }
}
