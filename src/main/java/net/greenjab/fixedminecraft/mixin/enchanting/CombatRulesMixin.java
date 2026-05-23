package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CombatRules.class)
public abstract class CombatRulesMixin {

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    private static void allowFullInvulnerability(float damage, float totalMagicArmor, CallbackInfoReturnable<Float> cir) {
        float realArmor = Mth.clamp(totalMagicArmor, 0.0F, 25.0F);
        cir.setReturnValue(damage * (1.0F - realArmor / 25.0F));
    }
}
