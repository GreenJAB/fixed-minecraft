package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean hasEnchantments();

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void enchantableHorseArmor(CallbackInfoReturnable<Boolean> cir) {
        if (((ItemStack)(Object)this).getItem() instanceof HorseArmorItem) {
            cir.setReturnValue(!this.hasEnchantments());
        }
    }
}
