package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(AreaEffectCloudEntity.class)
public class AreaEffectCloudEntityMixin {
    @Inject(method = "tick", at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;entrySet()Ljava/util/Set;"
    ))
    private void waterAreaEffect(CallbackInfo ci) {
        AreaEffectCloudEntity AECE = (AreaEffectCloudEntity) (Object)this;
        if (AECE.getPotion() == Potions.WATER) {
            this.applyWater();
        }
    }


    @Unique
    private void applyWater() {
        AreaEffectCloudEntity AECE = (AreaEffectCloudEntity) (Object)this;
        Box box = AECE.getBoundingBox();
        Predicate<LivingEntity> AFFECTED_BY_WATER = entity -> entity.hurtByWater() || entity.isOnFire();
        for (LivingEntity livingEntity : AECE.getWorld().getEntitiesByClass(LivingEntity.class, box, AFFECTED_BY_WATER)) {
            double d = AECE.squaredDistanceTo(livingEntity);
            if (d < AECE.getWidth()*AECE.getWidth()) {
                if (livingEntity.hurtByWater()) {
                    livingEntity.damage(AECE.getDamageSources().indirectMagic(AECE, AECE.getOwner()), 1.0F);
                }

                if (livingEntity.isOnFire() && livingEntity.isAlive()) {
                    livingEntity.extinguishWithSound();
                }
            }
        }

        for (AxolotlEntity axolotlEntity : AECE.getWorld().getNonSpectatingEntities(AxolotlEntity.class, box)) {
            axolotlEntity.hydrateFromPotion();
        }
    }
}
