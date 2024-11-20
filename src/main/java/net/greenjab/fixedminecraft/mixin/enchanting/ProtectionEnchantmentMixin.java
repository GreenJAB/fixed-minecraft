package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {

    @Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
    private void removeExclusivity(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "getProtectionAmount", at = @At("HEAD"), cancellable = true)
    private void separateProtections(int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        ProtectionEnchantment instance = (ProtectionEnchantment)(Object)this;
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            cir.setReturnValue(0);
        } else if (instance.protectionType == ProtectionEnchantment.Type.ALL
                   && !source.isIn(DamageTypeTags.IS_FIRE)
                   && !source.isIn(DamageTypeTags.IS_FALL)
                   && !source.isIn(DamageTypeTags.IS_EXPLOSION)
                   && !source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            cir.setReturnValue(level);
        } else if (instance.protectionType == ProtectionEnchantment.Type.FIRE && source.isIn(DamageTypeTags.IS_FIRE)) {
            cir.setReturnValue(level * 2);
        } else if (instance.protectionType == ProtectionEnchantment.Type.FALL && source.isIn(DamageTypeTags.IS_FALL)) {
            cir.setReturnValue(level * 3);
        } else if (instance.protectionType == ProtectionEnchantment.Type.EXPLOSION && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            cir.setReturnValue(level * 2);
        } else {
            cir.setReturnValue( instance.protectionType == ProtectionEnchantment.Type.PROJECTILE && source.isIn(DamageTypeTags.IS_PROJECTILE) ? level * 2 : 0);
        }
    }
}
