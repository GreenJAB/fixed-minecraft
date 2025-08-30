package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class CopperFireBlock extends AbstractFireBlock {
    public static final MapCodec<CopperFireBlock> CODEC = createCodec(CopperFireBlock::new);

    @Override
    public MapCodec<CopperFireBlock> getCodec() {
        return CODEC;
    }

    public CopperFireBlock(AbstractBlock.Settings settings) {
        super(settings, 2.0F);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            WorldView world,
            ScheduledTickView tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            Random random
    ) {
        return this.canPlaceAt(state, world, pos) ? this.getDefaultState() : Blocks.AIR.getDefaultState();
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return isCopperBase(world.getBlockState(pos.down()));
    }

    public static boolean isCopperBase(BlockState state) {
        return state.getBlock().getName().toString().toLowerCase().contains("copper");
    }

    @Override
    protected boolean isFlammable(BlockState state) {
        return true;
    }
}
