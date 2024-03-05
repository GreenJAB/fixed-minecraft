package net.green_jab.fixed_minecraft.mixin;

import com.mojang.serialization.MapCodec;
import net.green_jab.fixed_minecraft.FixedMinecraft;
import net.green_jab.fixed_minecraft.block.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.state.property.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilMixin /*extends FallingBlock*/ {

        private static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    /*public AnvilMixin(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends FallingBlock> getCodec() {
        return null;
    }*/


    @Inject(method = "getLandingState", at = @At("HEAD"),cancellable = true)
    private static void injected(BlockState fallingState, CallbackInfoReturnable cir) {
        if (fallingState.isOf(ModBlocks.NETHERITE_ANVIL)) {
            cir.setReturnValue((BlockState)ModBlocks.CHIPPED_NETHERITE_ANVIL.getDefaultState().with(FACING, fallingState.get(FACING)));
        }
        if (fallingState.isOf(ModBlocks.CHIPPED_NETHERITE_ANVIL)) {
            cir.setReturnValue((BlockState)ModBlocks.DAMAGED_NETHERITE_ANVIL.getDefaultState().with(FACING, fallingState.get(FACING)));
        }

    }
}
