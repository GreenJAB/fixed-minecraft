package net.greenjab.fixedminecraft.mixin.effects;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin {
    @Shadow
    private PotionContents potionContents;

    @Inject(method = "serverTick", at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;entrySet()Ljava/util/Set;"
    ))
    private void waterAreaEffect(CallbackInfo ci, @Local(argsOnly = true) ServerLevel serverLevel) {
        PotionContents potionContentsComponent = this.potionContents;
        if (potionContentsComponent.is(Potions.WATER)) {
            this.applyWater(serverLevel);
        }
        if (potionContentsComponent.is(Potions.WATER)) {
            this.applyWater(serverLevel);
        }
        if (potionContentsComponent.is(Potions.AWKWARD)) {
            this.applyAwkward();
        }
    }


    @Unique
    private void applyWater(ServerLevel serverWorld) {
        AreaEffectCloud AECE = (AreaEffectCloud) (Object)this;
        AABB box = AECE.getBoundingBox();
        Predicate<LivingEntity> AFFECTED_BY_WATER = entity -> entity.isSensitiveToWater() || entity.isOnFire();
        for (LivingEntity livingEntity : AECE.level().getEntitiesOfClass(LivingEntity.class, box, AFFECTED_BY_WATER)) {
            double d = AECE.distanceToSqr(livingEntity);
            if (d < AECE.getBbWidth()*AECE.getBbWidth()) {
                if (livingEntity.isSensitiveToWater()) {
                    livingEntity.hurtServer(serverWorld, AECE.damageSources().indirectMagic(AECE, AECE.getOwner()), 1.0F);
                }

                if (livingEntity.isOnFire() && livingEntity.isAlive()) {
                    livingEntity.extinguishFire();
                }
            }
        }

        for (Axolotl axolotlEntity : AECE.level().getEntitiesOfClass(Axolotl.class, box)) {
            axolotlEntity.rehydrate();
        }
    }

    @Unique
    private void applyAwkward() {
        AreaEffectCloud AECE = (AreaEffectCloud) (Object)this;
        AABB box = AECE.getBoundingBox();

        for (Piglin piglinEntity : AECE.level().getEntitiesOfClass(Piglin.class, box)) {
            piglinEntity.setImmuneToZombification(true);
        }
        for (Hoglin hoglinEntity : AECE.level().getEntitiesOfClass(Hoglin.class, box)) {
            hoglinEntity.setImmuneToZombification(true);
        }
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean lingerAddition(LivingEntity instance, MobEffectInstance newEffect, Entity source) {
        if (instance.hasEffect(newEffect.getEffect())) {
            MobEffectInstance current = instance.getEffect(newEffect.getEffect());
            if (current!=null && current.getAmplifier() == newEffect.getAmplifier()) {
                return instance.addEffect(new MobEffectInstance(newEffect.getEffect(), current.getDuration() + Mth.ceil(
                        newEffect.getDuration() / 3.0), newEffect.getAmplifier(), newEffect.isAmbient(), newEffect.isVisible(), newEffect.showIcon()), source);

            }
        }
        return instance.addEffect(new MobEffectInstance(newEffect), source);
    }
}
