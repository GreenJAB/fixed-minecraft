package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", ordinal = 0), index = 0)
    private Slot bannersOnHead(Slot par1, @Local(argsOnly = true) Player owner, @Local(argsOnly = true) Inventory inventory,
                           @Local int i, @Local EquipmentSlot slot, @Local Identifier emptyIcon) {
        return new ArmorSlot(inventory,owner, slot, 39 - i, 8, 8 + i * 18, emptyIcon) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                if (slot == EquipmentSlot.HEAD && stack.is(ItemTags.BANNERS)) {
                    return true;
                }
                return slot == owner.getEquipmentSlotForItem(stack);
            }
        };
    }

}
