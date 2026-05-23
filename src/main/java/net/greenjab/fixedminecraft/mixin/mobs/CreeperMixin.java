package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Creeper.class)
public abstract class CreeperMixin {

    @Redirect(method = "spawnLingeringCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private void notInfiniteEffect(AreaEffectCloud areaEffectCloudEntity, MobEffectInstance effect){
        if (effect.getDuration() == -1) {
            effect = new MobEffectInstance(effect.getEffect(), 20 * 300, effect.getAmplifier());
        }
        areaEffectCloudEntity.addEffect(effect);
    }

    @ModifyArg(method = "spawnLingeringCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setRadiusOnUse(F)V"))
    private float noShrinkOnUse(float radiusOnUse){
        return 0;
    }

}
