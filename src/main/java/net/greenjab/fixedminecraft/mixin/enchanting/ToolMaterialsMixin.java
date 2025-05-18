package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ToolMaterials.class)
public class ToolMaterialsMixin {

    /*@WrapOperation(
            method = "<clinit>",
            at = @At(value = "NEW",target = "(Ljava/lang/String;ILnet/minecraft/registry/tag/TagKey;IFFILjava/util/function/Supplier;)Lnet/minecraft/item/ToolMaterials;")
    )
    private static ToolMaterials goldToolsLastLonger(String inverseTag, int itemDurability, TagKey miningSpeed, int attackDamage,
                                                     float enchantability, float repairIngredient, int string, Supplier i,
                                                     Operation<ToolMaterials> original) {
        if (itemDurability == 32) {
            return original.call(inverseTag, 59, miningSpeed, attackDamage,
                    enchantability, repairIngredient, string, i);
        }
        if (itemDurability == 2031) {
            return original.call(inverseTag, itemDurability, miningSpeed, attackDamage, enchantability, repairIngredient, string, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));
        }
        return original.call(inverseTag, itemDurability, miningSpeed, attackDamage,
                enchantability, repairIngredient, string, i);
    }*/
    /*@Redirect(
            method = "M",
            at = @At(value = "NEW",target = "(Ljava/lang/String;ILnet/minecraft/registry/tag/TagKey;IFFILjava/util/function/Supplier;)Lnet/minecraft/item/ToolMaterials;")
    )
    private static ToolMaterials goldToolsLastLonger(String inverseTag, int itemDurability, TagKey miningSpeed, int attackDamage,
                                                     float enchantability, float repairIngredient, int string, Supplier i) {
        if (itemDurability == 32) {
            return original.call(inverseTag, 59, miningSpeed, attackDamage,
                    enchantability, repairIngredient, string, i);
        }
        if (itemDurability == 2031) {
            return original.call(inverseTag, itemDurability, miningSpeed, attackDamage, enchantability, repairIngredient, string, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));
        }
        return original.call(inverseTag, itemDurability, miningSpeed, attackDamage,
                enchantability, repairIngredient, string, i);
    }*/

    @Inject(method = "getDurability", at = @At(
            value = "RETURN"), cancellable = true)
    private void goldDura(CallbackInfoReturnable<Integer> cir){
        if (cir.getReturnValue()==32) cir.setReturnValue(64);
    }
}
