package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private static final float MODIFIER = 0.1F;
    @Unique
    private static final float DEG = (float) (Math.PI / 180F);

    @Shadow
    protected int riptideTicks;


    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow
    public abstract @Nullable StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Reduces water drag when using riptide.
     */
    @ModifyExpressionValue(
            method = "travel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z",
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

    @Redirect(method = "tickFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z"))
    private boolean cancelElytraInLiquid(LivingEntity instance, RegistryEntry<StatusEffect> effect) {
        if (instance instanceof PlayerEntity) {
            return !(!instance.hasStatusEffect(effect) &&
                     (instance.getWorld().getDifficulty().getId()>1?!instance.isWet():!instance.isTouchingWater()) &&
                     !instance.isInLava() &&
                     CustomData.getData(instance, "airTime") > 15);
        } else {
            return !(!instance.hasStatusEffect(effect) &&
                     (instance.getWorld().getDifficulty().getId()>1?!instance.isWet():!instance.isTouchingWater()) &&
                     !instance.isInLava());
        }
    }

    @ModifyConstant(method = "jump", constant = @Constant(doubleValue = 0.2))
    private double speedJump(double constant) {
        float i = 0;
        if (this.hasStatusEffect(StatusEffects.SPEED)) {
            i += 1+ this.getStatusEffect(StatusEffects.SPEED).getAmplifier();
        }
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            i +=0.5f*( 1+ this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier());
        }
        return constant+0.05F*i;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"))
    private void cancelElytraOnHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof PlayerEntity) {
            if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                CustomData.setData(LE, "airTime", -25);
            }
        }
    }

    @ModifyConstant(method = "getJumpBoostVelocityModifier", constant = @Constant(floatValue = 1.0f))
    private float betterJumpBoost(float original){
        return 2.0f;
    }

}
