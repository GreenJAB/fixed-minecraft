package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.block.CopperFireBlock;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFireBlock.class)
public class AbstractFireBlockMixin {

    @Inject(method = "getState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/SoulFireBlock;isSoulBase(Lnet/minecraft/block/BlockState;)Z"),
            cancellable = true
    )
    private static void isCopperFire(BlockView world, BlockPos pos, CallbackInfoReturnable<BlockState> cir, @Local BlockState blockState) {
        if (CopperFireBlock.isCopperBase(blockState)){
            cir.setReturnValue(BlockRegistry.COPPER_FIRE.getDefaultState());
        }
    }

}
