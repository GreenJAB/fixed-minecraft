package net.green_jab.fixed_minecraft.mixin;

import net.green_jab.fixed_minecraft.FixedMinecraft;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    /*@Inject(method = "getMaxUseTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isSnack()Z"),cancellable = true)
    //@Inject(method = "getMaxUseTime", at = @At("HEAD"),cancellable = true)
    private void ModifyFoodEatTimes(ItemStack stack, CallbackInfoReturnable cir) {
        if (stack.getItem().toString().toLowerCase().contains("golden")) {
            cir.setReturnValue(80);
        }
        cir.setReturnValue(4+7*stack.getFoodComponent().getHunger());
    }*/

    //@Inject(method = "getMaxUseTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isSnack()Z"),cancellable = true)
    @Inject(method = "getMaxUseTime", at = @At("HEAD"),cancellable = true)
    private void ModifyFoodEatTimes(ItemStack stack, CallbackInfoReturnable cir) {
        if (stack.getItem().isFood()) {
            cir.setReturnValue(8 + 8 * stack.getFoodComponent().getHunger());
            if (stack.getItem().toString().contains("golden")) cir.setReturnValue(100);
        }
    }
}
