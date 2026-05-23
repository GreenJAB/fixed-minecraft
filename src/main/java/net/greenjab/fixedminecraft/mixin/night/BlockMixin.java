package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(
            value = "HEAD"
    ))
    private static void nightFarming(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity breaker, ItemStack tool,
                                     CallbackInfo ci) {
        if (level instanceof ServerLevel && breaker != null) {
            if ( state == Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7) ||
                 state == Blocks.CARROTS.defaultBlockState().setValue(CropBlock.AGE, 7) ||
                 state == Blocks.POTATOES.defaultBlockState().setValue(CropBlock.AGE, 7) ||
                 state == Blocks.BEETROOTS.defaultBlockState().setValue(BeetrootBlock.AGE, 3) ||
                 state == Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 3) ||
                 state == Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, 2)) {
            if (level.getBrightness(LightLayer.SKY, pos) > 10) {
                MoonPhase moonPhase = (level).environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, breaker.blockPosition());
                if (level.isDarkOutside() && moonPhase.index() == 2) {
                    Block.getDrops(state, (ServerLevel) level, pos, blockEntity, breaker, tool).forEach((stack) -> Block.popResource(level, pos, stack));
                    state.spawnAfterBreak((ServerLevel) level, pos, tool, true);
                }
            }
        }
        }
    }
}
