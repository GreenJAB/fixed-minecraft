package net.greenjab.fixedminecraft.mixin.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {


    @Shadow
    @Final
    private PlayerEntity player;

    @Redirect(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;increment(I)V"))
    private void cancelItemDoublingForFletchingTable(ItemStack itemStack, int amount) {
        if (!this.player.currentScreenHandler.toString().toLowerCase().contains("fletch")) itemStack.increment(amount);
    }

}
