package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {


    @Inject(method = "isPrimaryItem", at = @At(value = "HEAD"), cancellable = true)
    private void otherChecks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment)(Object)this;
        Item item = stack.getItem();
        if (item instanceof AnimalArmorItem) {
            cir.setReturnValue( enchantment.isAcceptableItem(Items.DIAMOND_BOOTS.getDefaultStack()) || enchantment.isAcceptableItem(Items.DIAMOND_CHESTPLATE.getDefaultStack()));
            cir.cancel();
        }
        if (item instanceof MapBookItem) {
            if (enchantment.effects().contains(EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "isAcceptableItem", at = @At(value = "HEAD"), cancellable = true)
    private void otherChecks2(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment)(Object)this;
        Item item = stack.getItem();
        if (item instanceof AnimalArmorItem) {
            cir.setReturnValue( enchantment.isAcceptableItem(Items.DIAMOND_BOOTS.getDefaultStack()) || enchantment.isAcceptableItem(Items.DIAMOND_CHESTPLATE.getDefaultStack()));
            cir.cancel();
        }
        if (item instanceof MapBookItem) {
            if (enchantment.effects().contains(EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }




    @ModifyExpressionValue(method = "getName", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/util/Formatting;GRAY:Lnet/minecraft/util/Formatting;"
    ))
    private static Formatting greenSuperName(Formatting original, @Local(argsOnly = true) RegistryEntry<Enchantment> enchantment, @Local(argsOnly = true) int level) {
        if (level > enchantment.value().getMaxLevel()) {
            return Formatting.GREEN;
        }
        return original;
    }
}
