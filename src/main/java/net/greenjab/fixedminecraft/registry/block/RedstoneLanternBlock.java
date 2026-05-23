package net.greenjab.fixedminecraft.registry.block;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.NonNull;

public class RedstoneLanternBlock extends LanternBlock {
    public static final MapCodec<RedstoneLanternBlock> CODEC = simpleCodec(RedstoneLanternBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final Map<BlockGetter, List<BurnoutEntry>> BURNOUT_MAP = new WeakHashMap<>();

    @Override
    public @NonNull MapCodec<? extends RedstoneLanternBlock> codec() {
        return CODEC;
    }

    public RedstoneLanternBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, false).setValue(WATERLOGGED, false).setValue(LIT, true));
    }

    @Override
    protected void onPlace(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos, @NonNull BlockState oldState, boolean notify) {
        this.update(world, pos, state);
    }

    private void update(Level world, BlockPos pos, BlockState state) {
        Orientation wireOrientation = this.getEmissionOrientation(world, state);

        for (Direction direction : Direction.values()) {
            world.updateNeighborsAt(pos.relative(direction), this, ExperimentalRedstoneUtils.withFront(wireOrientation, direction));
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(@NonNull BlockState state, @NonNull ServerLevel world, @NonNull BlockPos pos, boolean moved) {
        if (!moved) {
            this.update(world, pos, state);
        }
    }

    @Override
    protected int getSignal(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull Direction direction) {
        return state.getValue(LIT) && (state.getValue(HANGING)?Direction.DOWN:Direction.UP) != direction ? 15 : 0;
    }

    protected boolean shouldUnpower(Level world, BlockPos pos, BlockState state) {
        if (state.getValue(HANGING)) {
            return world.hasSignal(pos.above(), Direction.UP);
        }
        return world.hasSignal(pos.below(), Direction.DOWN);
    }

    @Override
    protected void tick(@NonNull BlockState state, @NonNull ServerLevel world, @NonNull BlockPos pos, @NonNull RandomSource random) {
        boolean bl = this.shouldUnpower(world, pos, state);
        List<BurnoutEntry> list = BURNOUT_MAP.get(world);

        while (list != null && !list.isEmpty() && world.getGameTime() - list.getFirst().time > 60L) {
            list.removeFirst();
        }

        if (state.getValue(LIT)) {
            if (bl) {
                world.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);
                if (isBurnedOut(world, pos, true)) {
                    world.levelEvent(LevelEvent.REDSTONE_TORCH_BURNOUT, pos, 0);
                    world.scheduleTick(pos, world.getBlockState(pos).getBlock(), 160);
                }
            }
        } else if (!bl && !isBurnedOut(world, pos, false)) {
            world.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, @NonNull Level world, @NonNull BlockPos pos, @NonNull Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        if (state.getValue(LIT) == this.shouldUnpower(world, pos, state) && !world.getBlockTicks().willTickThisTick(pos, this)) {
            world.scheduleTick(pos, this, 2);
        }
    }

    @Override
    protected boolean isSignalSource(@NonNull BlockState state) {
        return true;
    }

    @Override
    public void animateTick(BlockState state, @NonNull Level world, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (state.getValue(LIT)) {
            double d = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double e = pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.5;
            double f = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            world.addParticle(DustParticleOptions.REDSTONE, d, e, f, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING);
        builder.add(LIT);
        builder.add(WATERLOGGED);
    }

    private static boolean isBurnedOut(Level world, BlockPos pos, boolean addNew) {
        List<BurnoutEntry> list = BURNOUT_MAP.computeIfAbsent(
                world, _ -> Lists.newArrayList()
        );
        if (addNew) {
            list.add(new BurnoutEntry(pos.immutable(), world.getGameTime()));
        }

        int i = 0;

        for (BurnoutEntry burnoutEntry : list) {
            if (burnoutEntry.pos.equals(pos)) {
                if (++i >= 8) {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    protected Orientation getEmissionOrientation(Level world, BlockState state) {

        return ExperimentalRedstoneUtils.initialOrientation(world, null, (state.getValue(HANGING)?Direction.DOWN:Direction.UP));
    }

    public static class BurnoutEntry {
        final BlockPos pos;
        final long time;

        public BurnoutEntry(BlockPos pos, long time) {
            this.pos = pos;
            this.time = time;
        }
    }

}
