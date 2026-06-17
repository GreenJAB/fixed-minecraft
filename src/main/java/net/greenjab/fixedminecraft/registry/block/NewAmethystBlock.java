package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class NewAmethystBlock extends AmethystBlock {
    public static final BooleanProperty LIT  = RedstoneTorchBlock.LIT;
    public static final MapCodec<NewAmethystBlock> CODEC = simpleCodec(NewAmethystBlock::new);
    public static final int[] RESONATION_NOTE_PITCHES = {0, 0, 2, 4, 6, 7, 9, 10, 12, 14, 16, 18, 19, 21, 22, 24};

    public NewAmethystBlock(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull MapCodec<NewAmethystBlock> codec()  {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(LIT, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
    }

    @Override
    protected void neighborChanged(
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            @NonNull Block sourceBlock,
            @Nullable Orientation wireOrientation,
            boolean notify
    ) {
        if (!level.isClientSide()) {
            boolean bl = state.getValue(LIT);
            if (bl != level.hasNeighborSignal(pos)) {
                if (bl) {
                    level.scheduleTick(pos, this, 2);
                } else {
                    level.setBlock(pos, state.cycle(LIT), UPDATE_CLIENTS);
                    int frequency = level.getBestNeighborSignal(pos);
                    level.gameEvent(VibrationSystem.getResonanceEventByFrequency(frequency), pos, GameEvent.Context.of(null, state));
                    int f = RESONATION_NOTE_PITCHES[frequency];
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0f, f);
                    if (!level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN)) ((ServerLevel) level).sendParticles(ParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 1, 0.0, 0.0, frequency / 15.0);
                }
            }
        }
    }

    @Override
    public void tick(BlockState state, @NonNull ServerLevel world, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (state.getValue(LIT) && !world.hasNeighborSignal(pos)) {
            world.setBlock(pos, state.cycle(LIT), UPDATE_CLIENTS);
        }
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}
