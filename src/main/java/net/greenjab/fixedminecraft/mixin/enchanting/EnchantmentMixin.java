package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = {"isPrimaryItem", "isAcceptableItem", "isSupportedItem"}, at = @At(value = "HEAD"), cancellable = true)
    private void otherChecks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment)(Object)this;
        Item item = stack.getItem();
        if (item instanceof AnimalArmorItem animalArmorItem) {
            if (animalArmorItem.getBreakSound() == SoundEvents.ENTITY_ITEM_BREAK) {
                cir.setReturnValue(enchantment.isAcceptableItem(Items.DIAMOND_BOOTS.getDefaultStack()) && !enchantment.isAcceptableItem(Items.FLINT_AND_STEEL.getDefaultStack()));
                cir.cancel();
            }
        }
        if (item instanceof MapBookItem) {
            if (enchantment.effects().contains(EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }


    @ModifyVariable(method = "slotMatches", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private EquipmentSlot feetEnchantsOnHorse(EquipmentSlot slot){
        if (slot==EquipmentSlot.BODY) {
            return EquipmentSlot.FEET;
        }
        return slot;
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
