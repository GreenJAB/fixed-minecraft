package net.greenjab.fixedminecraft.mixin;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.state.property.DirectionProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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


    @Inject(method = "getLandingState", at = @At("HEAD"), cancellable = true)
    private static void injected(BlockState fallingState, CallbackInfoReturnable cir) {
        // this code does nothing as the netherite anvil is never and instance of an AnvilBlock
        // if (fallingState.isOf(ModBlocks.NETHERITE_ANVIL)) {
        //     cir.setReturnValue((BlockState)ModBlocks.CHIPPED_NETHERITE_ANVIL.getDefaultState().with(FACING, fallingState.get(FACING)));
        // }
        // if (fallingState.isOf(ModBlocks.CHIPPED_NETHERITE_ANVIL)) {
        //     cir.setReturnValue((BlockState)ModBlocks.DAMAGED_NETHERITE_ANVIL.getDefaultState().with(FACING, fallingState.get(FACING)));
        // }

    }
}
