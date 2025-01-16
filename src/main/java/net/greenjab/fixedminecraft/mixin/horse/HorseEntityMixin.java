package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.HorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HorseEntity.class)
public class HorseEntityMixin {


    /*@ModifyExpressionValue(method = "getArmorType", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet1(EquipmentSlot original){
        return EquipmentSlot.FEET;
    }

    @ModifyExpressionValue(method = "equipArmor", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet2(EquipmentSlot original){
        return EquipmentSlot.FEET;
    }

    @ModifyExpressionValue(method = "updateSaddle", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet3(EquipmentSlot original){
        return EquipmentSlot.FEET;
    }*/
}
