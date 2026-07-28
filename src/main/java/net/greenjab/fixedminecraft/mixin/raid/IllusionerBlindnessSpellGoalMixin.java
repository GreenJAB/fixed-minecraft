package net.greenjab.fixedminecraft.mixin.raid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.monster.illager.Illusioner$IllusionerBlindnessSpellGoal")
public abstract class IllusionerBlindnessSpellGoalMixin {

    @WrapOperation(method = "performSpellCasting", at = @At(value = "NEW", target = "(Lnet/minecraft/core/Holder;I)Lnet/minecraft/world/effect/MobEffectInstance;"))
    private MobEffectInstance nauseaSpell(Holder<MobEffect> effect, int duration, Operation<MobEffectInstance> original) {
        return original.call(MobEffects.NAUSEA, 240);
    }
}
