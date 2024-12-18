package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private static final float MODIFIER = 0.1F;
    @Unique
    private static final float DEG = (float) (Math.PI / 180F);

    @Shadow
    protected int riptideTicks;

    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow
    @Nullable
    public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Reduces water drag when using riptide.
     */
    @ModifyExpressionValue(
            method = "travel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z",
            ordinal = 1
    )
    )
    private boolean boostWhenRiptide(boolean original) {
        return original || this.riptideTicks>0;
    }

    /**
     * Applies constant acceleration when using riptide and touching water.
     */
    @Inject(
            method = "tickMovement", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;tickRiptide(Lnet/minecraft/util/math/Box;Lnet/minecraft/util/math/Box;)V"
    )
    )
    private void accelerateWhenRiptide(CallbackInfo ci) {
        if (!isTouchingWater()) return;
        float f = getYaw();
        float g = getPitch();
        float h = -MathHelper.sin(f * DEG) * MathHelper.cos(g * DEG);
        float k = -MathHelper.sin(g * DEG);
        float l = MathHelper.cos(f * DEG) * MathHelper.cos(g * DEG);
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        h *= MODIFIER / m;
        k *= MODIFIER / m;
        l *= MODIFIER / m;
        addVelocity(h, k, l);
    }

    @Redirect(method = "tickFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
    private boolean cancelElytraInLiquid(LivingEntity instance, StatusEffect effect) {
        return !(!instance.hasStatusEffect(effect) && !instance.isWet() && !instance.isInLava() &&
                 CustomData.getData(instance, "airTime") > 15);
    }

    @ModifyConstant(method = "jump", constant = @Constant(floatValue = 0.2F))
    private float speedJump(float constant) {
        float i = 0;
        if (this.hasStatusEffect(StatusEffects.SPEED)) {
            i += 1+ this.getStatusEffect(StatusEffects.SPEED).getAmplifier();
        }
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            i +=0.5f*( 1+ this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier());
        }
        return constant+0.05F*i;
    }
}
