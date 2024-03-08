package net.greenjab.fixedminecraft.mixin;

import net.greenjab.fixedminecraft.blocks.BlockRegistry;
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

         if (fallingState.isOf(BlockRegistry.INSTANCE.getNETHERITE_ANVIL())) {
             cir.setReturnValue((BlockState)BlockRegistry.INSTANCE.getCHIPPED_NETHERITE_ANVIL().getDefaultState().with(FACING, fallingState.get(FACING)));
         }
         if (fallingState.isOf(BlockRegistry.INSTANCE.getCHIPPED_NETHERITE_ANVIL())) {
             cir.setReturnValue((BlockState)BlockRegistry.INSTANCE.getDAMAGED_NETHERITE_ANVIL().getDefaultState().with(FACING, fallingState.get(FACING)));
         }

    }
}
