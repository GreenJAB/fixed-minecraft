package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.block.CopperFireBlock;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {

    @Inject(method = "getState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SoulFireBlock;canSurviveOnBlock(Lnet/minecraft/world/level/block/state/BlockState;)Z"),
            cancellable = true
    )
    private static void isCopperFire(BlockGetter level, BlockPos pos, CallbackInfoReturnable<BlockState> cir, @Local BlockState belowState) {
        if (CopperFireBlock.isCopperBase(belowState)){
            cir.setReturnValue(BlockRegistry.COPPER_FIRE.defaultBlockState());
        }
    }

}
