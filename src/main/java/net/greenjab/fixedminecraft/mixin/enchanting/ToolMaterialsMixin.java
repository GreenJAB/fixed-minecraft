package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ToolMaterial.class)
public class ToolMaterialsMixin {

    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "NEW",target = "(Lnet/minecraft/registry/tag/TagKey;IFFILnet/minecraft/registry/tag/TagKey;)Lnet/minecraft/item/ToolMaterial;")
            )
    private static ToolMaterial goldToolsLastLonger(TagKey<Block> tagKey, int i, float f, float g, int j, TagKey<Block> tagKey2,
                                                     Operation<ToolMaterial> original) {
        if (i == 32) { //gold
            return original.call(tagKey, 48, f, g, j, tagKey2);
        }
        if (i == 190) { //copper
            return original.call(tagKey, 750, 4.0f, g, j, tagKey2);
        }
        return original.call(tagKey, i, f, g, j, tagKey2);
    }
}
