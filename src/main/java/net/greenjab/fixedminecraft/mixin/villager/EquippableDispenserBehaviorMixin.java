package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.dispenser.EquippableDispenserBehavior;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPointer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EquippableDispenserBehavior.class)
public class EquippableDispenserBehaviorMixin {

    @Inject(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;split(I)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private static void leatherGearOnVillagers(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> cir, @Local LivingEntity livingEntity, @Local EquipmentSlot equipmentSlot){
        if (livingEntity instanceof VillagerEntity VE) {
            if (!(armor.isIn(ItemTags.DYEABLE)) || !(VE.getVillagerData().getLevel()-1 > equipmentSlot.getEntitySlotId())) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
