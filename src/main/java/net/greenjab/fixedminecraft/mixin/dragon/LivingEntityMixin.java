package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 7))
    private boolean dontSlowdownEnderdragon(DamageSource source, TagKey<DamageType> tag) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof EnderDragonEntity || source.getAttacker() instanceof PhantomEntity) {
            return true;
        } else {
            return source.isIn(tag);
        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 5))
    private void aggroEndermenToPlayer(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir,
                                  @Local Entity entity) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof EndermiteEntity) {
            if (entity instanceof EndermanEntity endermanEntity) {
                LivingEntity livingEntity = endermanEntity.getWorld().getClosestPlayer(
                        TargetPredicate.createAttackable().setBaseMaxDistance(150.0).ignoreVisibility(),
                        endermanEntity, endermanEntity.getX(), endermanEntity.getY(), endermanEntity.getZ());
                endermanEntity.setTarget(livingEntity);
                endermanEntity.setAngerTime(999999);
            }
        }
    }
}
