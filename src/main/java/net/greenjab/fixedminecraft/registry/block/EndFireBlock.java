package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class EndFireBlock extends BaseFireBlock {

    public EndFireBlock(Properties settings) {
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
        return isEndStoneBase(world.getBlockState(pos)) && world.getBlockState(pos).isFaceSturdy(world, pos, Direction.UP);
    }

    public static boolean isEndStoneBase(BlockState state) {
        return state.getBlock().getName().toString().toLowerCase().contains("end_stone");
    }

    @Override
    protected boolean canBurn(@NonNull BlockState state) {
        return true;
    }

    @Override
    protected void onPlace(final @NonNull BlockState state, final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull BlockState oldState, final boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        level.scheduleTick(pos, this, getFireTickDelay(level.getRandom()));
    }

    private static int getFireTickDelay(final RandomSource random) {
        return 30 + random.nextInt(10);
    }

    @Override
    protected void tick(@NonNull BlockState state, final ServerLevel level, final @NonNull BlockPos pos, final @NonNull RandomSource random) {
        level.scheduleTick(pos, this, getFireTickDelay(level.getRandom()));
        if (level.getRandom().nextInt(10)==0) level.removeBlock(pos, false);
    }
}
