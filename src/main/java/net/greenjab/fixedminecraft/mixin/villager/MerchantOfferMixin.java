package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.world.item.trading.MerchantOffer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin {
    @Redirect(method = "updateDemand", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/trading/MerchantOffer;uses:I", opcode = Opcodes.GETFIELD, ordinal = 0))
    private int strongerDemand(MerchantOffer instance) {
        int u = instance.getUses();
        int m = instance.getMaxUses();
        int i = instance.getPriceMultiplier()<0.1f?2:1;
        if (u>0) {
            return (int)((u/(m+0.0f)) * 10*i) + (m-u);
        } else {
            return Math.max(-instance.getDemand(), -2*i) + (m-u);
        }
    }
}
