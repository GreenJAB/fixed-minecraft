package net.greenjab.fixedminecraft.mixin.raid;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Illusioner.class)
public abstract class IllusionerMixin {

    @Redirect(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;getY()D"))
    private double blindnessArrow(AbstractArrow instance){
        ((Arrow)instance).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80));
        return instance.getY();
    }

    @ModifyArg(method = "createAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", ordinal = 0), index = 1)
    private static double slowerBaseSpeed(double baseValue){
        return 0.35;
    }

}
