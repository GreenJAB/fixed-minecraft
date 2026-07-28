package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.CustomData;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private static final float MODIFIER = 0.1F;
    @Unique
    private static final float DEG = (float) (Math.PI / 180F);

    @Shadow
    protected int autoSpinAttackTicks;


    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract @Nullable MobEffectInstance getEffect(Holder<MobEffect> effect);

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    /**
     * Reduces water drag when using riptide.
     */
    @ModifyExpressionValue(
            method = "travelInWater", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z"
    )
    )
    private boolean boostWhenRiptide(boolean original) {
        return original || this.autoSpinAttackTicks>0;
    }

    /**
     * Applies constant acceleration when using riptide and touching water.
     */
    @Inject(
            method = "aiStep", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;checkAutoSpinAttack(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/phys/AABB;)V"
    )
    )
    private void accelerateWhenRiptide(CallbackInfo ci) {
        if (!isInWater()) return;
        float f = getYRot();
        float g = getXRot();
        float h = -Mth.sin(f * DEG) * Mth.cos(g * DEG);
        float k = -Mth.sin(g * DEG);
        float l = Mth.cos(f * DEG) * Mth.cos(g * DEG);
        float m = Mth.sqrt(h * h + k * k + l * l);
        h *= MODIFIER / m;
        k *= MODIFIER / m;
        l *= MODIFIER / m;
        push(h, k, l);
    }

    @WrapOperation(method = "canGlide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z"))
    private boolean cancelElytraInLiquid(LivingEntity instance, Holder<MobEffect> effect, Operation<Boolean> original) {
        if (instance instanceof Player) {
            return !(!original.call(instance, effect) &&
                     (FixedMinecraft.gameRules.elytra_fly_in_rain==0?!instance.isInWaterOrRain():!instance.isInWater()) &&
                     !instance.isInLava() &&
                     CustomData.getData(instance, "airTime") > FixedMinecraft.gameRules.elytra_deployment_ticks);
        } else return !(!original.call(instance, effect) && instance.isInWater() && !instance.isInLava());
    }

    @ModifyConstant(method = "jumpFromGround", constant = @Constant(doubleValue = 0.2))
    private double speedJump(double constant) {
        float i = 0;
        if (this.hasEffect(MobEffects.SPEED)) i += 1+ this.getEffect(MobEffects.SPEED).getAmplifier();
        if (this.hasEffect(MobEffects.JUMP_BOOST)) i +=0.5f*( 1+ this.getEffect(MobEffects.JUMP_BOOST).getAmplifier());
        return constant+0.05F*i;
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    private void cancelElytraOnHit(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir){
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof Player && FixedMinecraft.gameRules.elytra_hit_cancel_ticks!=0) {
            if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                CustomData.setData(LE, "airTime", FixedMinecraft.gameRules.elytra_deployment_ticks-FixedMinecraft.gameRules.elytra_hit_cancel_ticks);
            }
        }
    }

    @ModifyConstant(method = "getJumpBoostPower", constant = @Constant(floatValue = 1.0f))
    private float betterJumpBoost(float original){
        return 2.0f;
    }

    @WrapOperation(method = "updateFallFlyingMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 elytraDrag(Vec3 instance, double xScale, double yScale, double zScale, Operation<Vec3> original) {
        if (FixedMinecraft.gameRules.elytra_drag) {
            double drag;
            int near = 0;
            for (int i = 0; i < 8; i++) {
                Vec3 v = new Vec3(11 * Mth.sin(DEG * i * 45), 11 * Mth.cos(DEG * i * 45), 0).yRot(DEG * this.getYRot());
                if (this.level().clip(new ClipContext(this.position(), this.position().add(v),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this)).distanceTo(this) < 100) near++;
            }
            drag = 0.00025 * (8 - near) * (8 - near) * (this.isInWaterOrRain() ? 2 : 1);
            return original.call(instance, 1 - drag, yScale, 1 - drag);
        }
        return original.call(instance, xScale, yScale, zScale);
    }

}
