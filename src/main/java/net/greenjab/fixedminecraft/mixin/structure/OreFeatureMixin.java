package net.greenjab.fixedminecraft.mixin.structure;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.OreFeature;

import static net.minecraft.world.level.levelgen.feature.Feature.isAdjacentToAir;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin {
    @ModifyExpressionValue(method = "canPlaceOre", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/RuleTest;test(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z"
    ))
    private static boolean genInTerracotta(boolean original, @Local(argsOnly = true) BlockState orePosState,
                                           @Local(argsOnly = true) Function<BlockPos, BlockState> blockGetter,
                                           @Local(argsOnly = true) BlockPos.MutableBlockPos orePos) {
        if (orePosState.is(BlockTags.TERRACOTTA) || orePosState == Blocks.SANDSTONE.defaultBlockState()) {
            if (!isAdjacentToAir(blockGetter, orePos)) {
                return true;
            }
        }
        return original;
    }
}
