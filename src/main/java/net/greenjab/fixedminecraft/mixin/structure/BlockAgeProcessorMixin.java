package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockAgeProcessor.class)
public abstract class BlockAgeProcessorMixin {
    @Inject(method = "maybeReplaceObsidian", at = @At(value = "HEAD"), cancellable = true)
    private void noCryingObisidan(RandomSource random, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(null);
    }
}
