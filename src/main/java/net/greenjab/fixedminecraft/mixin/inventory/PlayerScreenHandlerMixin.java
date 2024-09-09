package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Prevents screen handler from dropping all crafting grid items.
 */
@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin {
    // Removed cancelled inject to make sure other functionality is called
    @WrapOperation(method = "onClosed", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;dropInventory(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/inventory/Inventory;)V"))
    protected void onClosed(PlayerScreenHandler instance, PlayerEntity playerEntity, Inventory inventory, Operation<Void> original) {
    }
}
