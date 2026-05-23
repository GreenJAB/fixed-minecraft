package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @ModifyExpressionValue(method = "doClick", at = @At(value = "INVOKE",
                                                        target = "Lnet/minecraft/world/inventory/Slot;safeTake(IILnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;"
    ))
    private ItemStack destroy0DurabilityItemThrow(ItemStack original) {
        if (original.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) == 5){
            return ItemStack.EMPTY;
        }
        return original;
    }

    @ModifyVariable(method = "setCarried", at = @At(value = "HEAD"), argsOnly = true)
    private ItemStack destroy0DurabilityItemClick(ItemStack carried) {
        if (carried.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) == 5){
            return ItemStack.EMPTY;
        }
        return carried;
    }

}
