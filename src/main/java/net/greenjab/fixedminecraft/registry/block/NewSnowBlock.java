package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class NewSnowBlock extends FallingBlock {
    public static final MapCodec<NewSnowBlock> CODEC = simpleCodec(NewSnowBlock::new);
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    private static final VoxelShape[] SHAPES_BY_LAYERS = Block.boxes(8, layers -> Block.column(16.0, 0.0, layers * 2));

    @Override
    public @NonNull MapCodec<NewSnowBlock> codec() {
        return CODEC;
    }

    public NewSnowBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    @Override
    protected boolean isPathfindable(@NonNull BlockState state, @NonNull PathComputationType type) {
        return type == PathComputationType.LAND && state.getValue(LAYERS) < 5;
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPES_BY_LAYERS[state.getValue(LAYERS)];
    }

    @Override
    protected @NonNull VoxelShape getCollisionShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPES_BY_LAYERS[state.getValue(LAYERS) - 1];
    }

    @Override
    protected @NonNull VoxelShape getBlockSupportShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos) {
        return SHAPES_BY_LAYERS[state.getValue(LAYERS)];
    }

    @Override
    protected @NonNull VoxelShape getVisualShape(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPES_BY_LAYERS[state.getValue(LAYERS)];
    }

    @Override
    protected boolean useShapeForLightOcclusion(@NonNull BlockState state) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos) {
        return state.getValue(LAYERS) == 8 ? 0.2F : 1.0F;
    }

    @Override
    protected boolean canSurvive(@NonNull BlockState state, LevelReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.below());
        if (blockState.is(BlockTags.CANNOT_SUPPORT_SNOW_LAYER)) {
            return false;
        } else {
            return blockState.is(BlockTags.CANNOT_SUPPORT_SNOW_LAYER) || Block.isFaceFull(blockState.getCollisionShape(world, pos.below()), Direction.UP) || blockState.is(this) && blockState.getValue(LAYERS) == 8;
        }
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
        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public int getDustColor(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos) {
        return -1;
    }

    @Override
    protected void randomTick(@NonNull BlockState state, ServerLevel world, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (world.getBrightness(LightLayer.BLOCK, pos) > 11) {
            if (!world.getBlockState(pos.below()).is(Blocks.CRYING_OBSIDIAN)) {
                dropResources(state, world, pos);
                world.removeBlock(pos, false);
            }
        }
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        int i = state.getValue(LAYERS);
        if (!context.getItemInHand().is(this.asItem()) || i >= 8) {
            return i == 1;
        } else {
            return !context.replacingClickedOnBlock() || context.getClickedFace() == Direction.UP;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (blockState.is(this)) {
            int i = blockState.getValue(LAYERS);
            return blockState.setValue(LAYERS, Math.min(8, i + 1));
        } else {
            return super.getStateForPlacement(ctx);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }
}
