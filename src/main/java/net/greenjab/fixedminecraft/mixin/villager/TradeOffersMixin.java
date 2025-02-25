package net.greenjab.fixedminecraft.mixin.villager;

import com.google.common.collect.ImmutableMap;
import net.greenjab.fixedminecraft.data.ModTags;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

        PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.FISHERMAN).replace(4, new TradeOffers.Factory[]{
                new TradeOffers.BuyItemFactory(Items.TROPICAL_FISH, 6, 12, 30),
                new TradeOffers.BuyItemFactory(Items.PUFFERFISH, 4, 12, 30)});

        PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.FISHERMAN).replace(5, new TradeOffers.Factory[]{
                new TradeOffers.TypeAwareBuyForOneEmeraldFactory(
                        1,
                        12,
                        30,
                        ImmutableMap.<VillagerType, Item>builder()
                                .put(VillagerType.PLAINS, Items.OAK_BOAT)
                                .put(VillagerType.TAIGA, Items.SPRUCE_BOAT)
                                .put(VillagerType.SNOW, Items.SPRUCE_BOAT)
                                .put(VillagerType.DESERT, Items.JUNGLE_BOAT)
                                .put(VillagerType.JUNGLE, Items.JUNGLE_BOAT)
                                .put(VillagerType.SAVANNA, Items.ACACIA_BOAT)
                                .put(VillagerType.SWAMP, Items.DARK_OAK_BOAT)
                                .build()
                ),
                FishingBook()});

        PROFESSION_TO_LEVELED_TRADE.replace(VillagerProfession.ARMORER, REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.ARMORER));
        PROFESSION_TO_LEVELED_TRADE.replace(VillagerProfession.CARTOGRAPHER, REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.CARTOGRAPHER));
    }

    @Unique
    private static TradeOffers.EnchantBookFactory FishingBook() {
        return new TradeOffers.EnchantBookFactory(30, 1, 3, ModTags.INSTANCE.getFISHING_TRADES());
    }

}
