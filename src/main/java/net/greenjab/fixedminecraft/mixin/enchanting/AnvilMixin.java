package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.BlockRegistry;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilMixin  {

    @Unique
    private static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

    @Inject(method = "getLandingState", at = @At("HEAD"), cancellable = true)
    private static void damageNetheriteAnvil(BlockState fallingState, CallbackInfoReturnable<BlockState> cir) {

        if (fallingState.isOf(BlockRegistry.NETHERITE_ANVIL)) {
            cir.setReturnValue(BlockRegistry.CHIPPED_NETHERITE_ANVIL
                    .getDefaultState()
                    .with(FACING, fallingState.get(FACING)));
        }
        if (fallingState.isOf(BlockRegistry.CHIPPED_NETHERITE_ANVIL)) {
            cir.setReturnValue(BlockRegistry.DAMAGED_NETHERITE_ANVIL
                    .getDefaultState()
                    .with(FACING, fallingState.get(FACING)));
        }
    }

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;createScreenHandlerFactory(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/screen/NamedScreenHandlerFactory;"))
    private void setNormalAnvil(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit,
                                CallbackInfoReturnable<ActionResult> cir){
        player.removeCommandTag("netherite_anvil");
        FixedMinecraft.INSTANCE.setNetheriteAnvil(false);

    }

    @Inject(method = "onUse", at = @At(value = "HEAD"), cancellable = true)
    private void repairAnvil(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit,
                                CallbackInfoReturnable<ActionResult> cir){
        for (ItemStack itemStack: player.getHandItems()) {
            if (itemStack.isOf(Items.IRON_BLOCK)) {
                if (state.isOf(Blocks.CHIPPED_ANVIL)) {
                    world.setBlockState(pos, Blocks.ANVIL.getStateWithProperties(state), Block.NOTIFY_ALL_AND_REDRAW);
                    world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, Blocks.ANVIL.getStateWithProperties(state)));
                    itemStack.decrementUnlessCreative(1, player);
                    cir.setReturnValue(ActionResult.SUCCESS);

                    cir.cancel();
                }
                if (state.isOf(Blocks.DAMAGED_ANVIL)) {
                    world.setBlockState(pos, Blocks.CHIPPED_ANVIL.getStateWithProperties(state), Block.NOTIFY_ALL_AND_REDRAW);
                    world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, Blocks.CHIPPED_ANVIL.getStateWithProperties(state)));
                    itemStack.decrementUnlessCreative(1, player);
                    cir.setReturnValue(ActionResult.SUCCESS);
                    cir.cancel();
                }
            }
        }
    }
}
