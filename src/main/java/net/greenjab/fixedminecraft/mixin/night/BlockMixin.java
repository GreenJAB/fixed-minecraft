package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", at = @At(
            value = "HEAD"
    ))
    private static void nightFarming(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool,
                                     CallbackInfo ci) {
        if (world instanceof ServerWorld) {
            if ( state == Blocks.WHEAT.getDefaultState().with(CropBlock.AGE, 7) ||
                 state == Blocks.CARROTS.getDefaultState().with(CropBlock.AGE, 7) ||
                 state == Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, 7) ||
                 state == Blocks.BEETROOTS.getDefaultState().with(BeetrootsBlock.AGE, 3) ||
                 state == Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 3) ||
                 state == Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, 2)) {
            if (world.getLightLevel(LightType.SKY, pos) > 10) {
                if (world.isNight() && world.getMoonPhase() == 2) {
                    Block.getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, tool).forEach((stack) -> Block.dropStack(world, pos, stack));
                    state.onStacksDropped((ServerWorld)world, pos, tool, true);
                }
            }
        }
        }
    }
}
