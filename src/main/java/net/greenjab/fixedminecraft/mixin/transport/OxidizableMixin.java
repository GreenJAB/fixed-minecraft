package net.greenjab.fixedminecraft.mixin.transport;

import com.google.common.collect.ImmutableBiMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.blocks.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Oxidizable.class)
public interface OxidizableMixin {
    @WrapOperation(
            method = "method_34740", at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableBiMap$Builder;build()Lcom/google/common/collect/ImmutableBiMap;"
    ), remap = false
    )
    private static ImmutableBiMap<Block, Block> addCopperRails(
            ImmutableBiMap.Builder<Block, Block> instance,
            Operation<ImmutableBiMap<Block, Block>> original) {
        BlockRegistry registry = BlockRegistry.INSTANCE;
        instance.put(registry.getCOPPER_RAIL(), registry.getEXPOSED_COPPER_RAIL());
        instance.put(registry.getEXPOSED_COPPER_RAIL(), registry.getWEATHERED_COPPER_RAIL());
        instance.put(registry.getWEATHERED_COPPER_RAIL(), registry.getOXIDIZED_COPPER_RAIL());
        return original.call(instance);
    }
}
