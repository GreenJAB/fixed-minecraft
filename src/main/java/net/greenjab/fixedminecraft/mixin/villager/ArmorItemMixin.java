package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    @Inject(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;split(I)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private static void leatherGearOnVillagers(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> cir, @Local LivingEntity livingEntity, @Local EquipmentSlot equipmentSlot){
        if (livingEntity instanceof VillagerEntity VE) {
            if (!(armor.getItem() instanceof DyeableArmorItem) || !(VE.getVillagerData().getLevel()>equipmentSlot.getArmorStandSlotId())) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
