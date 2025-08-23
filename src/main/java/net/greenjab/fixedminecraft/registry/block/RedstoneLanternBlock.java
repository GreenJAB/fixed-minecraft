package net.greenjab.fixedminecraft.registry.block;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class RedstoneLanternBlock extends LanternBlock {
    public static final MapCodec<RedstoneLanternBlock> CODEC = createCodec(RedstoneLanternBlock::new);
    public static final BooleanProperty LIT = Properties.LIT;
    //public static final BooleanProperty HANGING = LanternBlock.HANGING;
    private static final int SCHEDULED_TICK_DELAY = 2;
    private static final Map<BlockView, List<BurnoutEntry>> BURNOUT_MAP = new WeakHashMap();

    @Override
    public MapCodec<? extends RedstoneLanternBlock> getCodec() {
        return CODEC;
    }

    public RedstoneLanternBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HANGING, false).with(WATERLOGGED, false).with(LIT, true));
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.update(world, pos, state);
    }

    private void update(World world, BlockPos pos, BlockState state) {
        WireOrientation wireOrientation = this.getEmissionOrientation(world, state);

        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this, OrientationHelper.withFrontNullable(wireOrientation, direction));
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!moved) {
            this.update(world, pos, state);
        }
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(LIT) && (state.get(HANGING)?Direction.DOWN:Direction.UP) != direction ? 15 : 0;
    }

    protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
        if (state.get(HANGING)) {
            return world.isEmittingRedstonePower(pos.up(), Direction.UP);
        }
        return world.isEmittingRedstonePower(pos.down(), Direction.DOWN);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean bl = this.shouldUnpower(world, pos, state);
        List<BurnoutEntry> list = BURNOUT_MAP.get(world);

        while (list != null && !list.isEmpty() && world.getTime() - list.get(0).time > 60L) {
            list.remove(0);
        }

        if (state.get(LIT)) {
            if (bl) {
                world.setBlockState(pos, state.with(LIT, false), Block.NOTIFY_ALL);
                if (isBurnedOut(world, pos, true)) {
                    world.syncWorldEvent(WorldEvents.REDSTONE_TORCH_BURNS_OUT, pos, 0);
                    world.scheduleBlockTick(pos, world.getBlockState(pos).getBlock(), 160);
                }
            }
        } else if (!bl && !isBurnedOut(world, pos, false)) {
            world.setBlockState(pos, state.with(LIT, true), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (state.get(LIT) == this.shouldUnpower(world, pos, state) && !world.getBlockTickScheduler().isTicking(pos, this)) {
            world.scheduleBlockTick(pos, this, 2);
        }
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            double d = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double e = pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.5;
            double f = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            world.addParticleClient(DustParticleEffect.DEFAULT, d, e, f, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HANGING);
        builder.add(LIT);
        builder.add(WATERLOGGED);
    }

    private static boolean isBurnedOut(World world, BlockPos pos, boolean addNew) {
        List<BurnoutEntry> list = BURNOUT_MAP.computeIfAbsent(
                world, worldx -> Lists.newArrayList()
        );
        if (addNew) {
            list.add(new BurnoutEntry(pos.toImmutable(), world.getTime()));
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
    protected WireOrientation getEmissionOrientation(World world, BlockState state) {

        return OrientationHelper.getEmissionOrientation(world, null, (state.get(HANGING)?Direction.DOWN:Direction.UP));
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
