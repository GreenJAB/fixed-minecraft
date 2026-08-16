package net.greenjab.fixedminecraft.mixin.effects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin {

    @Shadow private boolean ambient;

    @WrapOperation(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;shouldApplyEffectTickThisTick(II)Z"))
    private boolean slowDownSaturationEffect(MobEffect effect, int tickCount, int amplification, Operation<Boolean> original, @Local(argsOnly = true) ServerLevel serverLevel) {
        if (effect.getDisplayName().getString().toLowerCase().contains("saturation")) {
            int i = (this.ambient?600:60) >> amplification;
            if (i > 0) return serverLevel.getGameTime() % i == 0;
            else return true;
        } else return original.call(effect, tickCount, amplification);
    }

    @WrapOperation(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;I)Z"))
    private boolean modifySaturationEffect(MobEffect effect, ServerLevel serverLevel, LivingEntity mob, int amplification,
                                           Operation<Boolean> original) {
        if (effect.getDisplayName().getString().toLowerCase().contains("saturation")) {
            if (!mob.level().isClientSide() && mob instanceof Player playerEntity) playerEntity.getFoodData().eat(+ 1, 0.0F);
            return true;
        } else return original.call(effect, serverLevel, mob, amplification);
    }
}
