package net.greenjab.fixedminecraft.mixin.raid;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(targets = "net.minecraft.entity.mob.IllusionerEntity$BlindTargetGoal")
class IllusionerBlindTargetGoalMixin {


    @Redirect(method = "castSpell", at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/registry/entry/RegistryEntry;I)Lnet/minecraft/entity/effect/StatusEffectInstance;"
    ))
    private StatusEffectInstance nauseaSpell(RegistryEntry<StatusEffect> effect, int duration) {
        return new StatusEffectInstance(StatusEffects.NAUSEA, 240);
    }
}
