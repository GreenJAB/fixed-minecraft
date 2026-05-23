package net.greenjab.fixedminecraft.mixin.structure;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "tickPrecipitation", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"
    ))
    private void snowUnderLeaves(BlockPos pos, CallbackInfo ci,
                                 @Local(ordinal = 1)BlockPos topPos,
                                 @Local(ordinal = 2)BlockPos belowPos,
                                 @Local Biome biome) {

        ServerLevel world = (ServerLevel) (Object)this;

        for (int i = 1; i < 16; i++) {
            if (world.getRandom().nextInt(4)>0) {
                continue;
            }

            BlockPos mutable = topPos.below(i);
            BlockState blockStateTest = world.getBlockState(mutable);
            if (blockStateTest.is(Blocks.SNOW) || blockStateTest.is(BlockTags.LEAVES)) {
                //skip
            } else if (blockStateTest.is(Blocks.AIR)) {
                BlockPos mutable2 = belowPos.below(i);
                if (biome.shouldFreeze(world, mutable2)) {
                    world.setBlockAndUpdate(mutable2, Blocks.ICE.defaultBlockState());
                }

                if (world.isRaining()) {
                    int h = world.getGameRules().get(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT);
                    if (h > 0 && biome.shouldSnow(world, mutable)) {
                        BlockState blockState = world.getBlockState(mutable);
                        if (blockState.is(Blocks.SNOW)) {
                            int j = blockState.getValue(SnowLayerBlock.LAYERS);
                            if (j < Math.min(h, 8)) {
                                BlockState blockState2 = blockState.setValue(SnowLayerBlock.LAYERS, j + 1);
                                Block.pushEntitiesUp(blockState, blockState2, world, mutable);
                                world.setBlockAndUpdate(mutable, blockState2);
                            }
                        } else {
                            world.setBlockAndUpdate(mutable, Blocks.SNOW.defaultBlockState());
                        }
                    }

                    Biome.Precipitation precipitation = biome.getPrecipitationAt(mutable2, world.getSeaLevel());
                    if (precipitation != Biome.Precipitation.NONE) {
                        BlockState blockState3 = world.getBlockState(mutable2);
                        blockState3.getBlock().handlePrecipitation(blockState3, world, mutable2, precipitation);
                    }
                }
            } else {
                i=20;
            }
        }
    }
}
