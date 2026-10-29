package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static net.minecraft.world.level.block.CopperBulbBlock.LIT;
import static net.minecraft.world.level.block.CopperBulbBlock.POWERED;

@Mixin(CopperBulbBlock.class)
public abstract class CopperBulbBlockMixin extends Block {
    public CopperBulbBlockMixin(Properties settings) {
        super(settings);
    }

    @Override
    public void tick(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        boolean signal = level.hasNeighborSignal(pos);
        BlockState newState = state;
        newState = newState.cycle(LIT);
        level.setBlockAndUpdate(pos, newState.setValue(POWERED, signal));
        level.playSound(null, pos, newState.getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
    }

    @WrapOperation(method = {"onPlace", "neighborChanged"}, at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CopperBulbBlock;checkAndFlip(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V"))
    private void undoMojankCringe(CopperBulbBlock instance, BlockState state, ServerLevel level, BlockPos pos, Operation<Void> original) {

        boolean signal = level.hasNeighborSignal(pos);
        if (signal != state.getValue(POWERED)) {
            BlockState newState = state;
            if (!state.getValue(POWERED)) {
                int delay = 0;
                if (state.toString().toLowerCase().contains("exposed")) delay = 1;
                if (state.toString().toLowerCase().contains("weathered")) delay = 2;
                if (state.toString().toLowerCase().contains("oxidized")) delay = 3;
                if (delay > 0) {
                    level.scheduleTick(pos, instance, delay, TickPriority.EXTREMELY_HIGH);
                } else {
                    newState = newState.cycle(LIT);
                    level.setBlock(pos, newState.setValue(POWERED, signal), 3);
                    level.playSound(null, pos, newState.getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
                }
            } else {
                level.setBlock(pos, newState.setValue(POWERED, signal), 3);
            }
        }
    }
}
