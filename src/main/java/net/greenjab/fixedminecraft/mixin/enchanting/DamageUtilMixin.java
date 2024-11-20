package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.entity.DamageUtil;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageUtil.class)
public class DamageUtilMixin {

    @Inject(method = "getInflictedDamage", at = @At("HEAD"), cancellable = true)
    private static void allowFullInvulnerability(float damageDealt, float protection, CallbackInfoReturnable<Float> cir) {
        float f = MathHelper.clamp(protection, 0.0F, 25.0F);
        cir.setReturnValue( damageDealt * (1.0F - f / 25.0F));
    }
}
