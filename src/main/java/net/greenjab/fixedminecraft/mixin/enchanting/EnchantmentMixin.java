package net.greenjab.fixedminecraft.mixin.enchanting;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(Enchantment.class)
public class EnchantmentMixin {


    @Inject(method = "isPrimaryItem", at = @At(value = "HEAD"), cancellable = true)
    private void otherChecks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment)(Object)this;
        Item item = stack.getItem();
        if (item instanceof AnimalArmorItem) {
           // if (enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING) {
            if (enchantment.effects().contains(EnchantmentEffectComponentTypes.ITEM_DAMAGE) || enchantment.effects().contains(EnchantmentEffectComponentTypes.REPAIR_WITH_XP)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
            //if (enchantment == Enchantments.THORNS) {
            //    return true;
            //}
            cir.setReturnValue( enchantment.isAcceptableItem(Items.DIAMOND_BOOTS.getDefaultStack()) || enchantment.isAcceptableItem(Items.DIAMOND_CHESTPLATE.getDefaultStack()));
            cir.cancel();
        }
        if (item instanceof MapBookItem) {
            if (enchantment.effects().contains(EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

}
