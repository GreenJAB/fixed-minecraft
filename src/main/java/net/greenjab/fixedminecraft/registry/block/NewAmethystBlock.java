package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AmethystBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import org.jetbrains.annotations.Nullable;

public class NewAmethystBlock extends AmethystBlock {
    public static final BooleanProperty LIT  = RedstoneTorchBlock.LIT;
    public static final MapCodec<NewAmethystBlock> CODEC = createCodec(NewAmethystBlock::new);
    public static final int[] RESONATION_NOTE_PITCHES = {0, 0, 2, 4, 6, 7, 9, 10, 12, 14, 16, 18, 19, 21, 22, 24};

    public NewAmethystBlock(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<NewAmethystBlock> getCodec()  {
        return CODEC;
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    protected void neighborUpdate(
            BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify
    ) {
        if (world != null) {
            if (!world.isClient) {
                boolean bl = state.get(LIT);
                if (bl != world.isReceivingRedstonePower(pos)) {
                    if (bl) {
                        world.scheduleBlockTick(pos, this, 4);
                    } else {
                        world.setBlockState(pos, state.cycle(LIT), NOTIFY_LISTENERS);
                        int frequency = world.getReceivedRedstonePower(pos);
                        world.emitGameEvent(Vibrations.getResonation(frequency), pos, GameEvent.Emitter.of(null, state));
                        int f = RESONATION_NOTE_PITCHES[frequency];
                        world.playSound(null ,pos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS,1.0f,f);
                    }
                }
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT) && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, state.cycle(LIT), NOTIFY_LISTENERS);
        }
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}
