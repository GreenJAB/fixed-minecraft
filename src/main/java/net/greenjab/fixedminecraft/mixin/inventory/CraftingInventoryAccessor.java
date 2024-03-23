package net.greenjab.fixedminecraft.mixin.inventory;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Allows accessing the inventory without sending packets (works fine anyway), but prevents NullPointerException on the server network handler.
 */
@Mixin(CraftingInventory.class)
public interface CraftingInventoryAccessor {
    @Accessor
    DefaultedList<ItemStack> getStacks();
}
