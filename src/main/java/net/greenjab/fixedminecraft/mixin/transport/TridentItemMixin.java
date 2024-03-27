package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Boosts riptide efficiency when used from water (balancing its extreme effectiveness during rain in combination with elytra)
 */
@Mixin(TridentItem.class)
public class TridentItemMixin {
    @WrapOperation(
            method = "onStoppedUsing", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"
    )
    )
    private void modifyRiptideStrength(PlayerEntity player, double x, double y, double z, Operation<Void> original) {
        //TODO configuration
        if (player.isTouchingWater()) {
            x *= 3F;
            z *= 3F;
        }
        original.call(player, x, y, z);
    }
}
