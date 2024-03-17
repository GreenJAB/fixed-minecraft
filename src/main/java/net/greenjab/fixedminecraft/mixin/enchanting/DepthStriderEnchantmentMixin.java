package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.enchantment.DepthStriderEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.BookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DepthStriderEnchantment.class)
public class DepthStriderEnchantmentMixin {

    @Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
    private void removeExclusivity(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((DepthStriderEnchantment)(Object)this != other);
    }
}
