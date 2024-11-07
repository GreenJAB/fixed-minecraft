package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.item.Items;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.minecraft.village.TradeOffers.PROFESSION_TO_LEVELED_TRADE;
import static net.minecraft.village.TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE;

@Mixin(TradeOffers.class)
public class TradeOffersMixin {

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void experimentalVillagers(CallbackInfo ci) {
        PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.TOOLSMITH).replace(3, new TradeOffers.Factory[]{
                new TradeOffers.BuyItemFactory(Items.FLINT, 30, 12, 20), new TradeOffers.SellEnchantedToolFactory(Items.IRON_AXE, 1, 3, 10, 0.2F),
                new TradeOffers.SellEnchantedToolFactory(Items.IRON_SHOVEL, 2, 3, 10, 0.2F), new TradeOffers.SellEnchantedToolFactory(Items.IRON_PICKAXE, 3, 3, 10, 0.2F),
                new TradeOffers.SellEnchantedToolFactory(Items.DIAMOND_HOE, 2, 3, 10, 0.2F)});

        PROFESSION_TO_LEVELED_TRADE.replace(VillagerProfession.ARMORER, REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.ARMORER));
        PROFESSION_TO_LEVELED_TRADE.replace(VillagerProfession.CARTOGRAPHER, REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.CARTOGRAPHER));
    }

}
