package net.greenjab.fixedminecraft.mixin.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconBlock.class)
public abstract class BeaconBlockMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void removeBeaconUI(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult,
                                CallbackInfoReturnable<InteractionResult> cir) {
        cir.setReturnValue(InteractionResult.FAIL);
    }
}
