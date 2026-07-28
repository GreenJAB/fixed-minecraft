package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin {

    @ModifyExpressionValue(method = "horseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;component(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Lnet/minecraft/world/item/Item$Properties;"))
    private Item.Properties enchantableHorseArmor(Item.Properties original, @Local(argsOnly = true) ArmorMaterial material) {
        return original.enchantable(1).durability(ArmorType.BODY.getDurability(material.durability())).repairable(material.repairIngredient());
    }

    @WrapOperation(method = "horseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/equipment/Equippable$Builder;setDamageOnHurt(Z)Lnet/minecraft/world/item/equipment/Equippable$Builder;"))
    private Equippable.Builder damagableHorseArmor(Equippable.Builder instance, boolean damageOnHurt, Operation<Equippable.Builder> original) {
        return original.call(instance, true);
    }
}
