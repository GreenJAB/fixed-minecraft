package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public class PotionEntityMixin {
    @Inject(method = "onCollision", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/projectile/thrown/PotionEntity;explodeWaterPotion(Lnet/minecraft/server/world/ServerWorld;)V"
    ))
    private void waterAreaEffect(HitResult hitResult, CallbackInfo ci, @Local PotionContentsComponent potion) {
        PotionEntity PE = (PotionEntity) (Object)this;
        if (PE.getStack().isOf(Items.LINGERING_POTION)) {
            this.applyLingeringPotion(potion);
        }
    }

    @Unique
    private void applyLingeringPotion(PotionContentsComponent potion) {
        PotionEntity PE = (PotionEntity) (Object)this;
        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(PE.getWorld(), PE.getX(), PE.getY(), PE.getZ());
        Entity entity = PE.getOwner();
        if (entity instanceof LivingEntity livingEntity) {
            areaEffectCloudEntity.setOwner(livingEntity);
        }
        areaEffectCloudEntity.setParticleType(ParticleTypes.SPLASH);
        areaEffectCloudEntity.setRadius(3.0F);
        areaEffectCloudEntity.setWaitTime(10);
        areaEffectCloudEntity.setDuration(600);
        areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
        areaEffectCloudEntity.setPotionContents(potion);

        PE.getWorld().spawnEntity(areaEffectCloudEntity);
    }
}
