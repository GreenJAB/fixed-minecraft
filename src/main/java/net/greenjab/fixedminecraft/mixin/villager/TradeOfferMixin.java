package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.village.TradeOffer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TradeOffer.class)
public abstract class TradeOfferMixin {
    @Redirect(method = "updateDemandBonus", at = @At(value = "FIELD", target = "Lnet/minecraft/village/TradeOffer;uses:I", opcode = Opcodes.GETFIELD, ordinal = 0))
    private int strongerDemand(TradeOffer instance) {
        int u = instance.getUses();
        int m = instance.getMaxUses();
        int i = instance.getPriceMultiplier()<0.1f?2:1;
        if (u>0) {
            return (int)((u/(m+0.0f)) * 10*i) + (m-u);
        } else {
            return Math.max(-instance.getDemandBonus(), -2*i) + (m-u);
        }
    }
}
