package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleContents.Mutable.class)
public abstract class BundleContentsMutableMixin {

    @ModifyExpressionValue(method = "tryInsert", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack getArrowFromBundle(ItemStack original) {
        BundleContents.Mutable b = (BundleContents.Mutable)(Object)this;
       while (original.count()>original.getMaxStackSize()) {
           b.items.addFirst(original.copyWithCount(original.getMaxStackSize()));
           original = original.copyWithCount(original.count()-original.getMaxStackSize());
       }
       return original;
   }
}
