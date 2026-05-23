package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BundleContents.Mutable.class)
public abstract class BundleContentsMutableMixin {
    @Shadow
    @Final
    public List<ItemStack> items;

    @ModifyExpressionValue(method = "tryInsert", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack getArrowFromBundle(ItemStack original) {
       while (original.count()>original.getMaxStackSize()) {
           this.items.add(0, original.copyWithCount(original.getMaxStackSize()));
           original = original.copyWithCount(original.count()-original.getMaxStackSize());
       }
       return original;
   }
}
