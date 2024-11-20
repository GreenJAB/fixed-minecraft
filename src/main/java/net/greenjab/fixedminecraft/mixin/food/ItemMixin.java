package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
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

            if (stack.isIn(ItemTags.PIGLIN_LOVED))
                cir.setReturnValue(80);
        }
    }

    @Inject(method = "canRepair", at = @At("HEAD"), cancellable = true)
    private void tridentRepair(ItemStack item, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (item.isOf(Items.TRIDENT)) {
            if (ingredient.isOf(Items.PRISMARINE_SHARD)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getEnchantability", at = @At("HEAD"), cancellable = true)
    private void enchantableHorseArmor(CallbackInfoReturnable<Integer> cir) {
        if (((Item)(Object)this) instanceof HorseArmorItem) {
            cir.setReturnValue(1);
        }
    }
}
