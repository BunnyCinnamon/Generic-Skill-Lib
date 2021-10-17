package arekkuusu.gsl.common.impl.entity.data;

import arekkuusu.gsl.api.GSLRegistries;
import arekkuusu.gsl.common.impl.entity.Strategic;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.Set;

public final class EntityDataSerializers {

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

    public static final EntityDataSerializer<Set<EntityBehavior<? extends Strategic>>> STRATEGY = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf pBuffer, Set<EntityBehavior<? extends Strategic>> pValue) {
            pBuffer.writeInt(pValue.size());
            for (EntityBehavior<?> strategy : pValue) {
                pBuffer.writeInt(strategy.getId());
            }
        }

        @Override
        public Set<EntityBehavior<? extends Strategic>> read(FriendlyByteBuf pBuffer) {
            Set<EntityBehavior<? extends Strategic>> array = Sets.newHashSet();
            for (int i = 0, arrayLength = pBuffer.readInt(); i < arrayLength; i++) {
                array.add((EntityBehavior<? extends Strategic>) EntityBehaviorInstances.ENTRIES.get(pBuffer.readInt()));
            }

            return array;
        }

        @Override
        public Set<EntityBehavior<? extends Strategic>> copy(Set<EntityBehavior<? extends Strategic>> pValue) {
            return Sets.newHashSet(pValue);
        }
    };

    static {
        net.minecraft.network.syncher.EntityDataSerializers.registerSerializer(BLOCK_POS_ARRAY);
        net.minecraft.network.syncher.EntityDataSerializers.registerSerializer(BLOCK_STATE_ARRAY);
        net.minecraft.network.syncher.EntityDataSerializers.registerSerializer(STRATEGY);
    }
}
