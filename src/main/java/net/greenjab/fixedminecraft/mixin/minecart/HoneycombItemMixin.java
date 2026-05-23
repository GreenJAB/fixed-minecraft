package net.greenjab.fixedminecraft.mixin.minecart;

import com.google.common.collect.ImmutableBiMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HoneycombItem.class)
public abstract class HoneycombItemMixin  {
    @ModifyExpressionValue(method = "lambda$static$0", at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableBiMap;builder()Lcom/google/common/collect/ImmutableBiMap$Builder;"
    ))
    private static ImmutableBiMap.Builder<Block, Block> addCopperRails(ImmutableBiMap.Builder<Block, Block> original) {
        return original
                .put(BlockRegistry.COPPER_RAIL, BlockRegistry.WAXED_COPPER_RAIL)
                .put(BlockRegistry.EXPOSED_COPPER_RAIL, BlockRegistry.WAXED_EXPOSED_COPPER_RAIL)
                .put(BlockRegistry.WEATHERED_COPPER_RAIL, BlockRegistry.WAXED_WEATHERED_COPPER_RAIL)
                .put(BlockRegistry.OXIDIZED_COPPER_RAIL, BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL);
    }
}
