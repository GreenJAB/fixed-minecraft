package net.greenjab.fixedminecraft.mixin.effects;

import net.minecraft.entity.projectile.thrown.LingeringPotionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LingeringPotionEntity.class)
public class LingeringPotionEntityMixin {

    @ModifyArg(method = "spawnAreaEffectCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/AreaEffectCloudEntity;setRadiusOnUse(F)V"))
    private float noShrinkOnUse(float radiusOnUse){
        return 0;
    }
}
