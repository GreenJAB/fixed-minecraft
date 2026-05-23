package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin {
    @WrapOperation(method = "removed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;clearContainer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/Container;)V"))
    protected void onClosed(InventoryMenu instance, Player playerEntity, Container inventory, Operation<Void> original) {
    }

    @WrapOperation(method = "removed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;clearContent()V"))
    protected void onClosed2(ResultContainer instance, Operation<Void> original) {
    }
}
