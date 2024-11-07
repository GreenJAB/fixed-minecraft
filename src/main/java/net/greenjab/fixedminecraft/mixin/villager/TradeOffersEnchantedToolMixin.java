package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TradeOffers.SellEnchantedToolFactory.class)
public class TradeOffersEnchantedToolMixin {
    @Inject(method = "create", at = @At(value = "RETURN"), cancellable = true)
    private void needDiamond(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir,
                           @Local(ordinal = 0) ItemStack itemStack,
                           @Local(ordinal = 1) ItemStack itemStack2) {
        if (itemStack.getItem().getName().toString().toLowerCase().contains("diamond")) {
            cir.setReturnValue(new TradeOffer(itemStack2, new ItemStack(Items.DIAMOND, 1), itemStack, 3, 30, 0.2F));
        } else {
            cir.setReturnValue(new TradeOffer(itemStack2, itemStack, 3, 1, 0.05F));
        }
    }
}
