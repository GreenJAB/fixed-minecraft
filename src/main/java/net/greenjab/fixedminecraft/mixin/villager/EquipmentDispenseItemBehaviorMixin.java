package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EquipmentDispenseItemBehavior.class)
public abstract class EquipmentDispenseItemBehaviorMixin {

    @Inject(method = "dispenseEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private static void leatherGearOnVillagers(BlockSource source, ItemStack dispensed, CallbackInfoReturnable<Boolean> cir,
                                               @Local LivingEntity target,
                                               @Local EquipmentSlot slot){
        if (target instanceof Villager VE) {
            if (!(dispensed.is(ItemTags.CAULDRON_CAN_REMOVE_DYE)) || !(VE.getVillagerData().level() - 1 > slot.getIndex())) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
