package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EndSpikeFeature.class)
public class EndSpikeFeatureMixin {

    @ModifyConstant(method = "generateSpike", constant = @Constant(intValue = 0, ordinal = 0))
    private int removeBowCheese(int constant) {
        return -1;
    }
    @ModifyConstant(method = "generateSpike", constant = @Constant(intValue = 3))
    private int removeBowCheese2(int constant) {
        return 4;
    }

    @ModifyVariable(method = "generateSpike", at = @At("STORE"), ordinal = 1)
    private boolean removeBowCheese3(boolean bl2, @Local(ordinal = 3)int m, @Local(ordinal = 4)int n) {
        if (Math.abs(m) == Math.abs(n) && Math.abs(m)==1) {
            return true;
        }
        return bl2;
    }

    @ModifyArg(method = "generateSpike", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/feature/EndSpikeFeature;setBlockState(Lnet/minecraft/world/ModifiableWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", ordinal = 2
    ), index = 2)
    private BlockState removeBowCheese2(BlockState blockState, @Local(ordinal = 3)int m, @Local(ordinal = 4)int n, @Local(ordinal = 5)int o) {
        if (o==-1) {
            if (Math.abs(m) != Math.abs(n) || Math.abs(m) ==1) {
                return Blocks.OBSIDIAN.getDefaultState();
            }
        }
        if (o==4) {
            return Blocks.IRON_TRAPDOOR.getDefaultState();
        }
        return blockState;
    }
}
