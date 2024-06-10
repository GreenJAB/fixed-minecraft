package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.item.BookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookItem.class)
public class BookItemMixin {

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void unenchantableBook(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "getEnchantability", at = @At("HEAD"), cancellable = true)
    private void noEnchantability(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
