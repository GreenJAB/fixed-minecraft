package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {


    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void enchantableHorseArmor(CallbackInfoReturnable<Boolean> cir) {
        if (((ItemStack)(Object)this).getItem() instanceof HorseArmorItem) {
            cir.setReturnValue(true);
        }
    }
}
