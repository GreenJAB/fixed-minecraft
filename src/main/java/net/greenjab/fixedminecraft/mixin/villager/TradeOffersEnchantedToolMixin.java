package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(TradeOffers.SellEnchantedToolFactory.class)
public class TradeOffersEnchantedToolMixin {
    @Shadow
    @Final
    private float multiplier;

    @Shadow
    @Final
    private int experience;

    @Inject(method = "create", at = @At(value = "RETURN"), cancellable = true)
    private void needDiamond(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir,
                           @Local ItemStack itemStack,
                           @Local TradedItem itemStack2) {
        if (!itemStack.getItem().getComponents().contains(DataComponentTypes.REPAIRABLE)) {
            cir.setReturnValue(new TradeOffer(itemStack2, itemStack, 3, this.experience, this.multiplier));
            return;
        }
        if (itemStack.getItem().getComponents().get(DataComponentTypes.REPAIRABLE).matches(Items.DIAMOND.getDefaultStack())){//.getName().toString().toLowerCase().contains("diamond")) {
            cir.setReturnValue(new TradeOffer(itemStack2, Optional.of(new TradedItem(Items.DIAMOND, 1)), itemStack, 3, this.experience, this.multiplier));
        } else {
            cir.setReturnValue(new TradeOffer(itemStack2, itemStack, 3, this.experience, this.multiplier));
        }
    }
}
