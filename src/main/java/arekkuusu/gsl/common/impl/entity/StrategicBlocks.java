package arekkuusu.gsl.common.impl.entity;

import arekkuusu.gsl.api.helper.MathHelper;
import arekkuusu.gsl.common.impl.entity.data.GSLDataSerializers;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StrategicBlocks extends Strategic {

    private static final EntityDataAccessor<BlockPos[][]> DATA_BLOCKS = SynchedEntityData.defineId(Strategic.class, GSLDataSerializers.BLOCK_POS_ARRAY);
    public double cursorProgress = 0;
    public int cursor = 0;

    public StrategicBlocks(EntityType<? extends StrategicBlocks> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_BLOCKS, new BlockPos[0][]);
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount < getWaitTime())
            updateSpread();
    }

    public void updateSpread() {
        BlockPos[][] positions = this.getEntityData().get(DATA_BLOCKS);
        double perTick = (double) (positions.length) / (double) (this.getWaitTime());
        this.cursorProgress += perTick;
        if(MathHelper.fuzzyCompare(this.cursorProgress += perTick, 1D) == 1) {
            this.cursor += this.cursorProgress;
            this.cursorProgress = 0;
        }
    }

    public void spreadOnTerrain() {
        BlockPos[][] positions = this.getEntityData().get(DATA_BLOCKS);
        int radius = (int) Math.floor(getWidth());
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> queue = new HashSet<>();
        BlockPos origin = getOnPos().below(); //Down into the ground if any
        BlockPos original = getValid(origin); //Look for the ground
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
                    BlockPos validated = getValid(pos);
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
        this.getEntityData().set(DATA_BLOCKS, positions);
    }

    @Nullable
    public BlockPos getValid(BlockPos pos) {
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

        if (!isSolid(mPos)) {
            for (int j = 0; ; j++) {
                if (j == 1) return null;
                mPos.move(Direction.DOWN);
                if (isSolid(mPos)) {
                    return mPos.immutable();
                }
            }
        } else if (isSolid(mPos.above())) {
            for (int j = 0; ; j++) {
                if (j == 1) return null;
                mPos.move(Direction.UP);
                if (!isSolid(mPos.above())) {
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

    public boolean isSolid(BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        return state.getCollisionShape(this.level, pos) != Shapes.empty();
    }

    public void addNext(Set<BlockPos> list, BlockPos origin) {
        list.add(origin.relative(Direction.NORTH));
        list.add(origin.relative(Direction.SOUTH));
        list.add(origin.relative(Direction.EAST));
        list.add(origin.relative(Direction.WEST));
    }

    public BlockPos[][] getBlocks() {
        return this.getEntityData().get(DATA_BLOCKS);
    }

    public BlockPos[][] getBlocksWithCursor() {
        return Arrays.copyOf(this.getBlocks(), Math.max(cursor, getBlocks().length));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }
}
