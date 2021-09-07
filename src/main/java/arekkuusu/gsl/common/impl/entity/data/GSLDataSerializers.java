package arekkuusu.gsl.common.impl.entity.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public final class GSLDataSerializers {

    public static final EntityDataSerializer<BlockPos[][]> BLOCK_POS_ARRAY = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf pBuffer, BlockPos[][] pValue) {
            pBuffer.writeInt(pValue.length);
            for (BlockPos[] subArray : pValue) {
                pBuffer.writeInt(subArray.length);
                for (BlockPos pos : subArray) {
                    pBuffer.writeInt(pos.getX());
                    pBuffer.writeInt(pos.getY());
                    pBuffer.writeInt(pos.getZ());
                }
            }
        }

        @Override
        public BlockPos[][] read(FriendlyByteBuf pBuffer) {
            int size = pBuffer.readInt();
            BlockPos[][] pValue = new BlockPos[size][0];
            for (int i = 0; i < pValue.length; i++) {
                int subSize = pBuffer.readInt();
                BlockPos[] subArray = new BlockPos[subSize];
                for (int j = 0; j < subArray.length; j++) {
                    subArray[j] = new BlockPos(pBuffer.readInt(), pBuffer.readInt(), pBuffer.readInt());
                }
                pValue[i] = subArray;
            }
            return pValue;
        }

        @Override
        public BlockPos[][] copy(BlockPos[][] pValue) {
            return Arrays.stream(pValue).map(a -> Arrays.copyOf(a, a.length)).toArray(BlockPos[][]::new);
        }
    };

    public static final EntityDataSerializer<BlockState[]> BLOCK_STATE_ARRAY = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf pBuffer, BlockState[] pValue) {
            pBuffer.writeInt(pValue.length);
            for (BlockState state : pValue) {
                pBuffer.writeInt(Block.getId(state));
            }
        }

        @Override
        public BlockState[] read(FriendlyByteBuf pBuffer) {
            int size = pBuffer.readInt();
            BlockState[] pValue = new BlockState[size];
            for (int i = 0; i < pValue.length; i++) {
                pValue[i] = Block.stateById(pBuffer.readInt());
            }
            return pValue;
        }

        @Override
        public BlockState[] copy(BlockState[] pValue) {
            return Arrays.copyOf(pValue, pValue.length);
        }
    };

    public static final EntityDataSerializer<Strategy> STRATEGY = new EntityDataSerializer<Strategy>() {
        @Override
        public void write(FriendlyByteBuf pBuffer, Strategy pValue) {
            pBuffer.writeInt(pValue.getId());
        }

        @Override
        public Strategy read(FriendlyByteBuf pBuffer) {
            return GSLStrategyInstances.ENTRIES.get(pBuffer.readInt());
        }

        @Override
        public Strategy copy(Strategy pValue) {
            return pValue;
        }
    };

    static {
        EntityDataSerializers.registerSerializer(BLOCK_POS_ARRAY);
        EntityDataSerializers.registerSerializer(BLOCK_STATE_ARRAY);
        EntityDataSerializers.registerSerializer(STRATEGY);
    }
}
