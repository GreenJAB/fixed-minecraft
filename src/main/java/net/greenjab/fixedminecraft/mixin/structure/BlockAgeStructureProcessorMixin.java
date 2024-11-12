package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.block.BlockState;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockAgeStructureProcessor.class)
public abstract class BlockAgeStructureProcessorMixin {
    @Inject(method = "processObsidian", at = @At(value = "HEAD"), cancellable = true)
    private void noCryingObisidan(Random random, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(null);
    }
}
