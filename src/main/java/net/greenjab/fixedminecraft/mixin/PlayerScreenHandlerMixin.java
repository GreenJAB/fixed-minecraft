package net.greenjab.fixedminecraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin {
    // This works, but it won't show the crafting result item unless you pick up and replace an item to update it.
    // Need to call `CraftingScreenHandler.updateResult(this, this.owner.getWorld(), this.owner, this.craftingInput, this.craftingResult);` when the menu is opened.
    // You can't do it in the constructor because of some networking stuff not being initialized yet.

    @Inject(method = "onClosed", at = @At(value = "INVOKE", target = "net/minecraft/screen/PlayerScreenHandler.dropInventory (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/inventory/Inventory;)V", shift = At.Shift.BEFORE), cancellable = true)
    protected void onClosed(PlayerEntity player, CallbackInfo ci) {
        ci.cancel();
    }
}
