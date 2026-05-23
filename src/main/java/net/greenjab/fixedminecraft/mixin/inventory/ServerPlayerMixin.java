package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "RETURN"))
    private void onGroundForLonger(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir,
                                   @Local ItemEntity entity) {
        if (!thrownFromHand && entity != null) {
            int diff = entity.level().getDifficulty().getId();
            if (diff == 2) entity.setExtendedLifetime();
            if (diff < 2) entity.setUnlimitedLifetime();
        }
    }

    //copyFrom
    @Inject(method = "transferInventoryXpAndScore", at = @At(value = "TAIL"))
    private void keepInventoryCraftingGrid(Player oldPlayer, CallbackInfo ci) {
        ServerPlayer SPE = (ServerPlayer) (Object)this;
        CraftingContainer craftingGrid = SPE.inventoryMenu.getCraftSlots();
        CraftingContainer craftingGridOriginal = SPE.connection.player.inventoryMenu.getCraftSlots();

        for (int i = 0; i < craftingGridOriginal.getContainerSize(); i++) {
            ItemStack itemStack = craftingGridOriginal.getItem(i);
            if (!itemStack.isEmpty()) {
                craftingGrid.setItem(i, itemStack);
            }
        }
    }
}
