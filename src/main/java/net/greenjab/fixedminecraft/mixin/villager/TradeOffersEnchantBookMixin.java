package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TradeOffers.EnchantBookFactory.class)
public class TradeOffersEnchantBookMixin {
    /*@ModifyArg(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/TradeOffer;<init>(Lnet/minecraft/village/TradedItem;Ljava/util/Optional;Lnet/minecraft/item/ItemStack;IIF)V"), index = 3)
    private int lessBooks(int maxUses) {
        return 3;
    }*/
}
