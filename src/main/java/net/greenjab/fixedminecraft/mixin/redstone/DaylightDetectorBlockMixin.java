package net.greenjab.fixedminecraft.mixin.redstone;

import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DaylightDetectorBlock.class)
public abstract class DaylightDetectorBlockMixin {
    @Inject(method = "updateState", at = @At(value = "TAIL"))
    private static void updateComparators(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        world.updateComparators(pos, state.getBlock());
    }

}
