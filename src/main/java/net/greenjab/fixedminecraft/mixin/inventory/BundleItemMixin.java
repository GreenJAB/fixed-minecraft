package net.greenjab.fixedminecraft.mixin.inventory;

import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BundleItem.class)
public class BundleItemMixin {
   @Redirect(method = "getItemOccupancy", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I"))
    private static int moreUnstackables(ItemStack stack) {
       int i = stack.getMaxCount();
       if (i == 1) {
           return i*4;
       } else if (i == 16) {
           return i*2;
       }
       return i;
   }
}