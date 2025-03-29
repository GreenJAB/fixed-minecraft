package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "dropItem", at = @At(value = "RETURN"))
    private void onGroundForLonger(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir, @Local ItemEntity itemEntity) {
        if (!retainOwnership && itemEntity != null) {
            int diff = itemEntity.getWorld().getDifficulty().getId();
            if (diff == 2) itemEntity.setCovetedItem();
            if (diff < 2) itemEntity.setNeverDespawn();
        }
    }
}
