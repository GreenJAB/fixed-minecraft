package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin{

    @Redirect(method = "spawnEffectsCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;addEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)V"))
    private void notInfiniteEffect(AreaEffectCloudEntity areaEffectCloudEntity, StatusEffectInstance statusEffectInstance){
        if (statusEffectInstance.getDuration()==-1) {
            statusEffectInstance = new StatusEffectInstance(statusEffectInstance.getEffectType(), 20*30, statusEffectInstance.getAmplifier());
        }
        areaEffectCloudEntity.addEffect(statusEffectInstance);
    }

    @ModifyArg(method = "spawnEffectsCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setRadiusOnUse(F)V"))
    private float noShrinkOnUse(float radiusOnUse){
        return 0;
    }

}
