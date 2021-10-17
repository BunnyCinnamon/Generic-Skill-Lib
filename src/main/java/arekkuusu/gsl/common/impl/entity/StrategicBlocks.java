package arekkuusu.gsl.common.impl.entity;

import arekkuusu.gsl.api.helper.MathHelper;
import arekkuusu.gsl.common.impl.entity.data.EntityDataSerializers;
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
import java.util.Set;

public class StrategicBlocks extends Strategic {

    private static final EntityDataAccessor<BlockPos[][]> DATA_BLOCKS = SynchedEntityData.defineId(Strategic.class, EntityDataSerializers.BLOCK_POS_ARRAY);
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
        if(tickCount < getGrowthDelay())
            updateSpread();
    }

    public void updateSpread() {
        BlockPos[][] positions = this.getEntityData().get(DATA_BLOCKS);
        double perTick = (double) (positions.length) / (double) (this.getGrowthDelay());
        this.cursorProgress += perTick;
        if(MathHelper.fuzzyCompare(this.cursorProgress += perTick, 1D) == 1) {
            this.cursor += this.cursorProgress;
            this.cursorProgress = 0;
        }
    }

    public BlockPos[][] getBlocks() {
        return this.getEntityData().get(DATA_BLOCKS);
    }

    public void setBlocks(BlockPos[][] blocks) {
        this.getEntityData().set(DATA_BLOCKS, blocks);
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
