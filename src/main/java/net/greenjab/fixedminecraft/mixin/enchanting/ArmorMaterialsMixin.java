package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.EnumMap;
import java.util.function.Supplier;

@Mixin(ArmorMaterials.class)
public class ArmorMaterialsMixin {

    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "NEW",target = "(Ljava/lang/String;ILjava/lang/String;ILjava/util/EnumMap;ILnet/minecraft/sound/SoundEvent;FFLjava/util/function/Supplier;)Lnet/minecraft/item/ArmorMaterials;")
            )
    private static ArmorMaterials modifiedToolMaterial(String name, int i, String name2, int durabilityMultiplier,
                                                       EnumMap<ArmorItem.Type, Integer> protectionAmounts, int enchantability, SoundEvent equipSound,
                                                       float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredientSupplier,
                                                       Operation<ArmorMaterials> original) {
        if (name.equals("NETHERITE")) {
            repairIngredientSupplier = () -> Ingredient.ofItems(Items.NETHERITE_SCRAP);
        }
        return original.call(name, i, name2, durabilityMultiplier, protectionAmounts, enchantability, equipSound, toughness, knockbackResistance, repairIngredientSupplier);
    }
}
