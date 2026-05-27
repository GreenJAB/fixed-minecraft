package net.greenjab.fixedminecraft.mixin.inventory;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContents.class)
public abstract class BundleContentsMixin {
   @Redirect(method = "getWeight", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemInstance;getMaxStackSize()I"))
    private static int moreUnstackables(ItemInstance instance) {
       int i = instance.getMaxStackSize();
       if (i == 1) {
           return i*4;
       } else if (i == 16) {
           return i*2;
       }
       if (instance.is(ItemTags.ARROWS)) return 4*i;
       return i;
   }

    @ModifyConstant(method = "getNumberOfItemsToShow", constant = @Constant(intValue = 12))
    private static int moreItemsShown(int constant) {
        return 64;
    }
    @ModifyConstant(method = "getNumberOfItemsToShow", constant = @Constant(intValue = 11))
    private static int moreItemsShown2(int constant) {
        return 63;
    }

    @Inject(method = "canItemBeInBundle", at = @At("HEAD"), cancellable = true)
    private static void noSuspiciousBlocksInBundle(ItemStack itemToAdd, CallbackInfoReturnable<Boolean> cir) {
        if (itemToAdd.is(Items.SUSPICIOUS_GRAVEL)||itemToAdd.is(Items.SUSPICIOUS_SAND)) cir.setReturnValue(false);
    }
}
