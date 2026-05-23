package net.greenjab.fixedminecraft.mixin.minecart;

import com.google.common.collect.ImmutableBiMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WeatheringCopper.class)
public interface WeatheringCopperMixin {
    @ModifyExpressionValue(
            method = "lambda$static$0", at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableBiMap;builder()Lcom/google/common/collect/ImmutableBiMap$Builder;",
            ordinal = 0
    )
    )
    private static ImmutableBiMap.Builder<Block, Block> addCopperRails(ImmutableBiMap.Builder<Block, Block> original) {
        return original
                .put(BlockRegistry.COPPER_RAIL, BlockRegistry.EXPOSED_COPPER_RAIL)
                .put(BlockRegistry.EXPOSED_COPPER_RAIL, BlockRegistry.WEATHERED_COPPER_RAIL)
                .put(BlockRegistry.WEATHERED_COPPER_RAIL, BlockRegistry.OXIDIZED_COPPER_RAIL);
    }
}
