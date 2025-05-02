package net.greenjab.fixedminecraft.mixin.netherite;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ArmorSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorSlot.class)
public abstract class ArmorSlotMixin {

    @ModifyExpressionValue(method = "canTakeItems", at = @At(value = "INVOKE",
                                                             target = "Lnet/minecraft/enchantment/EnchantmentHelper;hasAnyEnchantmentsWith(Lnet/minecraft/item/ItemStack;Lnet/minecraft/component/ComponentType;)Z"
    ))
    private boolean noNetheriteHarvest(boolean original, @Local ItemStack itemStack) {
        if (original)
            if (itemStack.isIn(ModTags.UNBREAKABLE) && itemStack.willBreakNextUse()) return false;
        return original;
    }
}
