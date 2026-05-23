package net.greenjab.fixedminecraft.mixin.netherite;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorSlot.class)
public abstract class ArmorSlotMixin {

    @ModifyExpressionValue(method = "mayPickup", at = @At(value = "INVOKE",
           target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;has(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/component/DataComponentType;)Z"
    ))
    private boolean noNetheriteHarvest(boolean original, @Local ItemStack itemStack) {
        if (original)
            if (itemStack.is(ModTags.UNBREAKABLE) && itemStack.nextDamageWillBreak()) return false;
        return original;
    }
}
