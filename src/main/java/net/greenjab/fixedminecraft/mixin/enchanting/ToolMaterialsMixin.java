package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(ToolMaterials.class)
public class ToolMaterialsMixin {

    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "NEW",target = "(Ljava/lang/String;IIIFFILjava/util/function/Supplier;)Lnet/minecraft/item/ToolMaterials;")
            )
    private static ToolMaterials modifiedToolMaterial(String name, int i, int miningLevel, int itemDurability,
                                                      float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient,
                                                      Operation<ToolMaterials> original) {
        switch (name) {
            case "GOLD":
                itemDurability = 59;
                miningLevel = MiningLevels.IRON;
                break;
            case "NETHERITE":
                repairIngredient = () -> Ingredient.ofItems(Items.NETHERITE_SCRAP);
                break;
            default: break;
        }
        return original.call(name, i, miningLevel, itemDurability, miningSpeed, attackDamage, enchantability, repairIngredient);
    }
}
