package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class CopperFireBlock extends BaseFireBlock {
    public static final MapCodec<CopperFireBlock> CODEC = simpleCodec(CopperFireBlock::new);

    @Override
    public @NonNull MapCodec<CopperFireBlock> codec() {
        return CODEC;
    }

    public CopperFireBlock(BlockBehaviour.Properties settings) {
        super(settings, 2.0F);
    }

    @Override
    protected @NonNull BlockState updateShape(
            @NonNull BlockState state,
            @NonNull LevelReader world,
            @NonNull ScheduledTickAccess tickView,
            @NonNull BlockPos pos,
            @NonNull Direction direction,
            @NonNull BlockPos neighborPos,
            @NonNull BlockState neighborState,
            @NonNull RandomSource random
    ) {
        return this.canSurvive(state, world, pos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean canSurvive(@NonNull BlockState state, LevelReader world, @NonNull BlockPos pos) {
        pos = pos.below();
        return isCopperBase(world.getBlockState(pos)) && world.getBlockState(pos).isFaceSturdy(world, pos, Direction.UP);
    }

    public static boolean isCopperBase(BlockState state) {
        return state.getBlock().getName().toString().toLowerCase().contains("copper");
    }

    @Override
    protected boolean canBurn(@NonNull BlockState state) {
        return true;
    }
}
