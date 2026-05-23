package net.greenjab.fixedminecraft.mixin.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Allows accessing the inventory without sending packets (works fine anyway), but prevents NullPointerException on the server network handler.
 */
@Mixin(TransientCraftingContainer.class)
public interface CraftingInventoryAccessor {
    @Accessor
    NonNullList<ItemStack> getItems();
}
