package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "canSprint", at = @At("HEAD"), cancellable = true)
    private void cancelSprintAt0Saturation(CallbackInfoReturnable cir) {
        PlayerEntity instance = (PlayerEntity)(Object)this;
        cir.setReturnValue(instance.hasVehicle() || (float)instance.getHungerManager().getSaturationLevel() > 0.0F || instance.getAbilities().allowFlying);
    }
}
