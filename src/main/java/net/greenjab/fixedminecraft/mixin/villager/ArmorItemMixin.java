package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

import static net.minecraft.village.TradeOffers.PROFESSION_TO_LEVELED_TRADE;
import static net.minecraft.village.TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE;

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
