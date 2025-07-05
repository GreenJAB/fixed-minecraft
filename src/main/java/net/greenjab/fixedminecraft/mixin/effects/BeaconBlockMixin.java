package net.greenjab.fixedminecraft.mixin.effects;

import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconBlock.class)
public class BeaconBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void removeBeaconUI(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit,
                                CallbackInfoReturnable<ActionResult> cir) {
        cir.setReturnValue(ActionResult.FAIL);
    }
}
