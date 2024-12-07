package net.greenjab.fixedminecraft.mixin.beacon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin {

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;canApplyUpdateEffect(II)Z"))
    private boolean slowDownSaturationEffect(StatusEffect effect , int duration, int amplifier) {
        StatusEffectInstance SEI = (StatusEffectInstance)(Object)this;
        if (effect.getTranslationKey() == StatusEffects.SATURATION.getTranslationKey()) {
            int i = 600 >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            } else {
                return true;
            }
        } else {
            return SEI.getEffectType().canApplyUpdateEffect(duration, amplifier);
        }
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;applyUpdateEffect(Lnet/minecraft/entity/LivingEntity;I)V"))
    private void modifySaturationEffect(StatusEffect effect, LivingEntity entity, int amplifier) {
        StatusEffectInstance SEI = (StatusEffectInstance)(Object)this;

        if (effect.getTranslationKey() == StatusEffects.SATURATION.getTranslationKey()) {
            if (!entity.getWorld().isClient && entity instanceof PlayerEntity playerEntity) {
                playerEntity.getHungerManager().add(+ 1, 0.0F);
            }
        } else {
            SEI.getEffectType().applyUpdateEffect(entity,amplifier);
        }
    }
}
