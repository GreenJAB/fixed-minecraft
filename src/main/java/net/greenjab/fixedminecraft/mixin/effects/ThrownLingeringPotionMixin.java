package net.greenjab.fixedminecraft.mixin.effects;

import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ThrownLingeringPotion.class)
public abstract class ThrownLingeringPotionMixin {

    @ModifyArg(method = "onHitAsPotion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setRadiusOnUse(F)V"))
    private float noShrinkOnUse(float radiusOnUse){
        return 0;
    }
}
