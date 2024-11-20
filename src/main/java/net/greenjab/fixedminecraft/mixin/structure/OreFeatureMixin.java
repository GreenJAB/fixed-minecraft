package net.greenjab.fixedminecraft.mixin.structure;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.OreFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

import static net.minecraft.world.gen.feature.Feature.isExposedToAir;


@Mixin(OreFeature.class)
public abstract class OreFeatureMixin {
    @ModifyExpressionValue(method = "shouldPlace", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/structure/rule/RuleTest;test(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/random/Random;)Z"
    ))
    private static boolean genInTerracotta(boolean original, @Local(argsOnly = true) BlockState blockState, @Local(argsOnly = true) Function<BlockPos, BlockState> posToState, @Local(
            argsOnly = true
    ) BlockPos.Mutable pos) {
        if (blockState.isIn(BlockTags.TERRACOTTA) || blockState == Blocks.SANDSTONE.getDefaultState()) {
            if (!isExposedToAir(posToState, pos)) {
                return true;
            }
        }
        return original;
    }
}
