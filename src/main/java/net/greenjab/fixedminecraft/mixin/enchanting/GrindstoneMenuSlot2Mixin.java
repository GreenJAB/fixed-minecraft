package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$3")
public abstract class GrindstoneMenuSlot2Mixin extends Slot {

    public GrindstoneMenuSlot2Mixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @ModifyExpressionValue(method = "mayPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;hasAnyEnchantments(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean book(boolean original, @Local(argsOnly = true) ItemStack itemStack) {
        if (FixedMinecraft.gameRules.grindstone_into_books) return original || itemStack.is(Items.BOOK);
        return original;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
