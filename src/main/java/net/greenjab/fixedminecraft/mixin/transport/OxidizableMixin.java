package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.block.Oxidizable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Oxidizable.class)
public interface OxidizableMixin {
    /*@ModifyExpressionValue(
            method = "method_34740", at = @At(
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
    }*/
}
