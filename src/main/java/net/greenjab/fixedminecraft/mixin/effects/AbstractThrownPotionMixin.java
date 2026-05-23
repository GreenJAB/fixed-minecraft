package net.greenjab.fixedminecraft.mixin.effects;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractThrownPotion.class)
public abstract class AbstractThrownPotionMixin {
    @Inject(method = "onHit", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/AbstractThrownPotion;onHitAsWater(Lnet/minecraft/server/level/ServerLevel;)V"
    ))
    private void waterAreaEffect(HitResult hitResult, CallbackInfo ci, @Local PotionContents potion) {
        AbstractThrownPotion PE = (AbstractThrownPotion) (Object)this;
        if (PE.getItem().is(Items.LINGERING_POTION)) {
            this.applyLingeringPotion(potion);
        }
    }

    @Inject(method = "onHit", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;is(Lnet/minecraft/core/Holder;)Z"
    ))
    private void piglinAwkwardEffect(HitResult hitResult, CallbackInfo ci) {
        AbstractThrownPotion PE = (AbstractThrownPotion) (Object)this;
        AABB box = PE.getBoundingBox().inflate(4.0, 2.0, 4.0);

        for (Piglin piglinEntity : PE.level().getEntitiesOfClass(Piglin.class, box)) {
            piglinEntity.setImmuneToZombification(true);
        }
        for (Hoglin hoglinEntity : PE.level().getEntitiesOfClass(Hoglin.class, box)) {
            hoglinEntity.setImmuneToZombification(true);
        }
    }

    @Unique
    private void applyLingeringPotion(PotionContents potion) {
        AbstractThrownPotion PE = (AbstractThrownPotion) (Object)this;
        AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(PE.level(), PE.getX(), PE.getY(), PE.getZ());
        Entity entity = PE.getOwner();
        if (entity instanceof LivingEntity livingEntity) {
            areaEffectCloudEntity.setOwner(livingEntity);
        }
        areaEffectCloudEntity.setCustomParticle(ParticleTypes.SPLASH);
        areaEffectCloudEntity.setRadius(3.0F);
        areaEffectCloudEntity.setWaitTime(10);
        areaEffectCloudEntity.setDuration(600);
        areaEffectCloudEntity.setRadiusPerTick(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
        areaEffectCloudEntity.setPotionContents(potion);

        PE.level().addFreshEntity(areaEffectCloudEntity);
    }
}
