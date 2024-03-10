package net.greenjab.fixedminecraft.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@SuppressWarnings("unchecked")
@Mixin(LivingEntity.class)
public class LivingEntityMixin  {


    @Redirect(method = "tickFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
        private boolean cancelElytraInLiquid(LivingEntity instance, StatusEffect effect) {
        return !(!instance.hasStatusEffect(effect) && !instance.isWet() && !instance.isInLava());
    }
}
