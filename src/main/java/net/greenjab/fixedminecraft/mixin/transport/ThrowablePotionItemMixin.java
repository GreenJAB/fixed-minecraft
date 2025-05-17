package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.ThrowablePotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ThrowablePotionItem.class)
public class ThrowablePotionItemMixin {
    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/PotionEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"), index = 4)
    private float longerLingeringThrows(float constant){
        ThrowablePotionItem TPI = (ThrowablePotionItem)(Object)this;
        if (TPI instanceof LingeringPotionItem) {
            return 0.85f;
        }
        return constant;
    }
}
