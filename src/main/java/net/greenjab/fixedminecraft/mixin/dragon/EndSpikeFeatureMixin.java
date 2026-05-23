package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndSpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EndSpikeFeature.class)
public abstract class EndSpikeFeatureMixin {

    @ModifyConstant(method = "placeSpike", constant = @Constant(intValue = 0, ordinal = 0))
    private int removeBowCheese(int constant) {
        return -1;
    }
    @ModifyConstant(method = "placeSpike", constant = @Constant(intValue = 3))
    private int removeBowCheese2(int constant) {
        return 4;
    }

    @ModifyVariable(method = "placeSpike", at = @At("STORE"), ordinal = 1)
    private boolean removeBowCheese3(boolean isZSide, @Local(ordinal = 4)int dx, @Local(ordinal = 5)int dz) {
        if (Math.abs(dx) == Math.abs(dz) && Math.abs(dx) == 1) {
            return true;
        }
        return isZSide;
    }

    @ModifyArg(method = "placeSpike", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/EndSpikeFeature;setBlock(Lnet/minecraft/world/level/LevelWriter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", ordinal = 2
    ), index = 2)
    private BlockState removeBowCheese2(BlockState blockState, @Local(ordinal = 4)int dx, @Local(ordinal = 5)int dz, @Local(ordinal = 6)int dy) {
        if (dy == -1) {
            if (Math.abs(dx) != Math.abs(dz) || Math.abs(dx) == 1) {
                return Blocks.OBSIDIAN.defaultBlockState();
            }
        }
        if (dy == 4) {
            return Blocks.IRON_TRAPDOOR.defaultBlockState();
        }
        return blockState;
    }
}
