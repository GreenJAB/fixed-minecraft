package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.village.TradeOffer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TradeOffer.class)
public abstract class TradeOfferMixin {
    @Redirect(method = "updateDemandBonus", at = @At(value = "FIELD", target = "Lnet/minecraft/village/TradeOffer;uses:I", opcode = Opcodes.GETFIELD, ordinal = 0))
    private int strongerDemand(TradeOffer instance) {
        return instance.getUses()*5;
    }
}
