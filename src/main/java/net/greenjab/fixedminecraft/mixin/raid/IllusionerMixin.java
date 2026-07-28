package net.greenjab.fixedminecraft.mixin.raid;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Illusioner.class)
public abstract class IllusionerMixin {

    @Inject(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/illager/Illusioner;getX()D"))
    private void blindnessArrow(LivingEntity target, float power, CallbackInfo ci, @Local AbstractArrow arrow){
        ((Arrow) arrow).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80));
    }

    @ModifyArg(method = "createAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", ordinal = 0), index = 1)
    private static double slowerBaseSpeed(double baseValue){
        return 0.35;
    }

}
