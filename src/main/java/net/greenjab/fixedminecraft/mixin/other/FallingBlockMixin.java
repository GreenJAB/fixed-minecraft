package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.block.NewSnowBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlock.class)
public class FallingBlockMixin {

    @Inject(method = "canFallThrough", at = @At("HEAD"), cancellable = true)
    private static void dontFallThroughFullSnow(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.isOf(Blocks.SNOW) && state.get(NewSnowBlock.LAYERS)==8) cir.setReturnValue(false);
    }

}
