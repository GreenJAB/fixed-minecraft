package net.greenjab.fixedminecraft.mixin.structure;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(SnowAndFreezeFeature.class)
public abstract class SnowAndFreezeFeatureMixin {
    @Inject(method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z"
    ))
    private void snowUnderLeaves(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir,
                                 @Local WorldGenLevel level,
                                 @Local(ordinal = 0)BlockPos.MutableBlockPos topPos,
                                 @Local(ordinal = 1)BlockPos.MutableBlockPos belowPos,
                                 @Local Biome biome) {
        for (int i = 1; i < 16; i++) {

            BlockPos mutable = topPos.below(i);

            BlockState blockStateTest = level.getBlockState(mutable);
            if (blockStateTest.is(Blocks.SNOW) || blockStateTest.is(BlockTags.LEAVES)) {
                //skip
            } else if (blockStateTest.is(Blocks.AIR)) {
                BlockPos mutable2 = belowPos.below(i);
                if (biome.shouldFreeze(level, mutable2, false)) {
                    level.setBlock(mutable2, Blocks.ICE.defaultBlockState(), Block.UPDATE_CLIENTS);
                }

                if (biome.shouldSnow(level, mutable)) {
                    level.setBlock(mutable, Blocks.SNOW.defaultBlockState(), Block.UPDATE_CLIENTS);
                    BlockState blockState = level.getBlockState(mutable2);
                    if (blockState.hasProperty(SnowyBlock.SNOWY)) {
                        level.setBlock(mutable2, blockState.setValue(SnowyBlock.SNOWY, true), Block.UPDATE_CLIENTS);
                    }
                }
            } else {
                i=20;
            }
        }
    }
}
