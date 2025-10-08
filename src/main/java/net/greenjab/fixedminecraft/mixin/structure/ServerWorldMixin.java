package net.greenjab.fixedminecraft.mixin.structure;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SnowyBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FreezeTopLayerFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "tickIceAndSnow", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/biome/Biome;canSetIce(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"
    ))
    private void snowUnderLeaves(BlockPos pos, CallbackInfo ci,
                                 @Local(ordinal = 1)BlockPos blockPos,
                                 @Local(ordinal = 2)BlockPos blockPos2,
                                 @Local Biome biome) {

        ServerWorld world = (ServerWorld) (Object)this;

        for (int i = 1; i < 16; i++) {
            if (world.random.nextInt(4)>0) {
                continue;
            }

            BlockPos mutable = blockPos.down(i);
            BlockState blockStateTest = world.getBlockState(mutable);
            if (blockStateTest.isOf(Blocks.SNOW) || blockStateTest.isIn(BlockTags.LEAVES)) {

            } else if (blockStateTest.isOf(Blocks.AIR)) {
                BlockPos mutable2 = blockPos2.down(i);
                if (biome.canSetIce(world, mutable2)) {
                    world.setBlockState(mutable2, Blocks.ICE.getDefaultState());
                }

                if (world.isRaining()) {
                    int h = world.getGameRules().getInt(GameRules.SNOW_ACCUMULATION_HEIGHT);
                    if (h > 0 && biome.canSetSnow(world, mutable)) {
                        BlockState blockState = world.getBlockState(mutable);
                        if (blockState.isOf(Blocks.SNOW)) {
                            int j = (Integer)blockState.get(SnowBlock.LAYERS);
                            if (j < Math.min(h, 8)) {
                                BlockState blockState2 = blockState.with(SnowBlock.LAYERS, j + 1);
                                Block.pushEntitiesUpBeforeBlockChange(blockState, blockState2, world, mutable);
                                world.setBlockState(mutable, blockState2);
                            }
                        } else {
                            world.setBlockState(mutable, Blocks.SNOW.getDefaultState());
                        }
                    }

                    Biome.Precipitation precipitation = biome.getPrecipitation(mutable2, world.getSeaLevel());
                    if (precipitation != Biome.Precipitation.NONE) {
                        BlockState blockState3 = world.getBlockState(mutable2);
                        blockState3.getBlock().precipitationTick(blockState3, world, mutable2, precipitation);
                    }
                }
            } else {
                i=20;
            }
        }
    }
}
