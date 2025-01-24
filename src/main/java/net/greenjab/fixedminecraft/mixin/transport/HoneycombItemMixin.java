package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.item.HoneycombItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {
    //TODO
    /*@ModifyExpressionValue(method = "method_34723", at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableBiMap;builder()Lcom/google/common/collect/ImmutableBiMap$Builder;"
    ))
    private static ImmutableBiMap.Builder<Block, Block> addCopperRails(ImmutableBiMap.Builder<Block, Block> original) {
        return original
                .put(BlockRegistry.COPPER_RAIL, BlockRegistry.WAXED_COPPER_RAIL)
                .put(BlockRegistry.EXPOSED_COPPER_RAIL, BlockRegistry.WAXED_EXPOSED_COPPER_RAIL)
                .put(BlockRegistry.WEATHERED_COPPER_RAIL, BlockRegistry.WAXED_WEATHERED_COPPER_RAIL)
                .put(BlockRegistry.OXIDIZED_COPPER_RAIL, BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL);
    }*/
}
