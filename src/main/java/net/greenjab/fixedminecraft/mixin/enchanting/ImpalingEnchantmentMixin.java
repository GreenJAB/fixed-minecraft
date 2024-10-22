package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.enchantment.ImpalingEnchantment;
import net.minecraft.entity.EntityGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ImpalingEnchantment.class)
public class ImpalingEnchantmentMixin {

    @Inject(method = "getAttackDamage", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/EntityGroup;AQUATIC:Lnet/minecraft/entity/EntityGroup;"
    ), cancellable = true)
    private void removeExclusivity(int level, EntityGroup group, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0f);
    }
}
