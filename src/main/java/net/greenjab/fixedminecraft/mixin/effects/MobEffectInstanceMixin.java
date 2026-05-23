package net.greenjab.fixedminecraft.mixin.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin {

    @Shadow
    private boolean ambient;

    @Redirect(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;shouldApplyEffectTickThisTick(II)Z"))
    private boolean slowDownSaturationEffect(MobEffect effect , int tickCount, int amplification) {
        if (effect.getDisplayName().getString().toLowerCase().contains("saturation")) {
            int i = (this.ambient?3000:60) >> amplification;
            if (i > 0) {
                return tickCount % i == 0;
            } else {
                return true;
            }
        } else {
            return effect.shouldApplyEffectTickThisTick(tickCount, amplification);
        }
    }

    @Redirect(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;I)Z"))
    private boolean modifySaturationEffect(MobEffect effect, ServerLevel serverLevel, LivingEntity mob, int amplification) {
        if (effect.getDisplayName().getString().toLowerCase().contains("saturation")) {
            if (!mob.level().isClientSide() && mob instanceof Player playerEntity) {
                playerEntity.getFoodData().eat(+ 1, 0.0F);
            }
        } else {
            return effect.applyEffectTick(serverLevel, mob, amplification);
        }
        return true;
    }
}
