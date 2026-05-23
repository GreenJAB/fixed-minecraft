package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.block.NewSnowBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin {

    @Inject(method = "isFree", at = @At("HEAD"), cancellable = true)
    private static void dontFallThroughFullSnow(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.SNOW) && state.getValue(NewSnowBlock.LAYERS)==8) cir.setReturnValue(false);
    }

}
