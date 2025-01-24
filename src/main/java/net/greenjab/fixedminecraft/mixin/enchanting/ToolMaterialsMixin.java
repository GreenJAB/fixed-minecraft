package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(ToolMaterial.class)
public class ToolMaterialsMixin {

    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "NEW",target = "(Lnet/minecraft/registry/tag/TagKey;IFFILnet/minecraft/registry/tag/TagKey;)Lnet/minecraft/item/ToolMaterial;")
            )
    private static ToolMaterial modifiedToolMaterial(TagKey<Block> tagKey, int i, float f, float g, int j, TagKey<Block> tagKey2,
                                                     Operation<ToolMaterial> original) {
        if (i == 32) {
            return original.call(tagKey, 59, f, g, j, tagKey2);
        }
        return original.call(tagKey, i, f, g, j, tagKey2);
    }
}
