package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.registry.BlockRegistry;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilMixin  {

    @Unique
    private static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    @Inject(method = "getLandingState", at = @At("HEAD"), cancellable = true)
    private static void damageNetheriteAnvil(BlockState fallingState, CallbackInfoReturnable<BlockState> cir) {

        if (fallingState.isOf(BlockRegistry.INSTANCE.getNETHERITE_ANVIL())) {
            cir.setReturnValue(BlockRegistry.INSTANCE.getCHIPPED_NETHERITE_ANVIL()
                    .getDefaultState()
                    .with(FACING, fallingState.get(FACING)));
        }
        if (fallingState.isOf(BlockRegistry.INSTANCE.getCHIPPED_NETHERITE_ANVIL())) {
            cir.setReturnValue(BlockRegistry.INSTANCE.getDAMAGED_NETHERITE_ANVIL()
                    .getDefaultState()
                    .with(FACING, fallingState.get(FACING)));
        }
    }

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;createScreenHandlerFactory(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/screen/NamedScreenHandlerFactory;"))
    private void setNormalAnvil(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit,
                                CallbackInfoReturnable<ActionResult> cir){
        player.removeCommandTag("netherite_anvil");
    }
}
