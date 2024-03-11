package net.greenjab.fixedminecraft.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void modifyFoodEatTimes(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem().isFood()) {
            cir.setReturnValue(8 + 8 * stack.getItem().getFoodComponent().getHunger());

            if (stack.getItem().toString().contains("golden"))
                cir.setReturnValue(80);
        }
    }
}
