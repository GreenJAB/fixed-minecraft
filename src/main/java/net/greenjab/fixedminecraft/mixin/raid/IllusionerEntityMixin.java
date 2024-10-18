package net.greenjab.fixedminecraft.mixin.raid;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@SuppressWarnings("unchecked")
@Mixin(IllusionerEntity.class)
public abstract class IllusionerEntityMixin {

    @Redirect(method = "shootAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;getY()D"))
    private double blindnessArrow(PersistentProjectileEntity instance){
        ((ArrowEntity)instance).addEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 80));
        return instance.getY();
    }

}
