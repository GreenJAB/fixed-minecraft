package net.greenjab.fixedminecraft.mixin.raid;

import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Mixin(targets = "net.minecraft.entity.mob.IllusionerEntity$BlindTargetGoal")
class IllusionerBlindTargetGoalMixin {


    @ModifyArgs(method = "castSpell", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z"
    ))
    private void nauseaSpell(Args args) {
        args.set(0, StatusEffects.NAUSEA);
        args.set(1, 240);
    }
}
