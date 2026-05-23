package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 5))
    private boolean dontSlowdownEnderdragon(DamageSource source, TagKey<DamageType> tag) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof EnderDragon || source.getEntity() instanceof Phantom) {
            return true;
        } else {
            return source.is(tag);
        }
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;resolveMobResponsibleForDamage(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void aggroEndermenToPlayer(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof Endermite) {
            Entity entity = source.getEntity();
            if (entity!=null) {
                if (entity instanceof EnderMan endermanEntity){
                    LivingEntity livingEntity = endermanEntity.level().getNearestPlayer(
                            endermanEntity.getX(), endermanEntity.getY(), endermanEntity.getZ(), 100.0,true);
                    endermanEntity.setTarget(livingEntity);
                    endermanEntity.setTimeToRemainAngry(999999);
                }
            }
        }
    }
}
