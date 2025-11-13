package net.greenjab.fixedminecraft.mixin.structure;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FreezeTopLayerFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(FreezeTopLayerFeature.class)
public abstract class FreezeTopLayerFeatureMixin {
    @Inject(method = "generate", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/biome/Biome;canSetIce(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Z)Z"
    ))
    private void snowUnderLeaves(FeatureContext<DefaultFeatureConfig> context, CallbackInfoReturnable<Boolean> cir,
                                        @Local StructureWorldAccess structureWorldAccess,
                                        @Local(ordinal = 0)BlockPos.Mutable mutable0, @Local(ordinal = 1)BlockPos.Mutable mutable02, @Local Biome biome) {
        for (int i = 1; i < 16; i++) {

            BlockPos mutable = mutable0.down(i);

            BlockState blockStateTest = structureWorldAccess.getBlockState(mutable);
            if (blockStateTest.isOf(Blocks.SNOW) || blockStateTest.isIn(BlockTags.LEAVES)) {
                //skip
            } else if (blockStateTest.isOf(Blocks.AIR)) {
                BlockPos mutable2 = mutable02.down(i);
                if (biome.canSetIce(structureWorldAccess, mutable2, false)) {
                    structureWorldAccess.setBlockState(mutable2, Blocks.ICE.getDefaultState(), Block.NOTIFY_LISTENERS);
                }

                if (biome.canSetSnow(structureWorldAccess, mutable)) {
                    structureWorldAccess.setBlockState(mutable, Blocks.SNOW.getDefaultState(), Block.NOTIFY_LISTENERS);
                    BlockState blockState = structureWorldAccess.getBlockState(mutable2);
                    if (blockState.contains(SnowyBlock.SNOWY)) {
                        structureWorldAccess.setBlockState(mutable2, blockState.with(SnowyBlock.SNOWY, true), Block.NOTIFY_LISTENERS);
                    }
                }
            } else {
                i=20;
            }
        }
    }
}
