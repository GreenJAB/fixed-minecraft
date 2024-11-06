package net.greenjab.fixedminecraft.mixin.villager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static net.minecraft.village.TradeOffers.PROFESSION_TO_LEVELED_TRADE;
import static net.minecraft.village.TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE;
import static net.minecraft.village.TradeOffers.REBALANCED_WANDERING_TRADER_TRADES;

@Mixin(TradeOffers.class)
public class TradeOffersMixin {
    /*
        VillagerProfession.ARMORER, copyToFastUtilMap(ImmutableMap.builder()
        .put(1, new TradeOffers.Factory[]{new TradeOffers.BuyItemFactory(Items.COAL, 15, 12, 2), new TradeOffers.BuyItemFactory(Items.IRON_INGOT, 5, 12, 2)})
        .put(2, new TradeOffers.Factory[]{
            TradeOffers.TypedWrapperFactory.of( new TradeOffers.SellItemFactory(Items.IRON_BOOTS, 4, 1, 12, 5, 0.05F), VillagerType.DESERT, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(Items.CHAINMAIL_BOOTS, 4, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(Items.IRON_HELMET, 5, 1, 12, 5, 0.05F), VillagerType.DESERT, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(Items.CHAINMAIL_HELMET, 5, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(Items.IRON_LEGGINGS, 7, 1, 12, 5, 0.05F), VillagerType.DESERT, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(Items.CHAINMAIL_LEGGINGS, 7, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(Items.IRON_CHESTPLATE, 9, 1, 12, 5, 0.05F), VillagerType.DESERT, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(Items.CHAINMAIL_CHESTPLATE, 9, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP)})
        .put(3, new TradeOffers.Factory[]{new TradeOffers.BuyItemFactory(Items.LAVA_BUCKET, 1, 12, 20), new TradeOffers.SellItemFactory(Items.SHIELD, 5, 1, 12, 10, 0.05F), new TradeOffers.SellItemFactory(Items.BELL, 36, 1, 12, 10, 0.2F)})
        .put(4, new TradeOffers.Factory[]{TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_BOOTS, Enchantments.THORNS, 1), 8, 1, 3, 15, 0.05F), VillagerType.DESERT),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_HELMET, Enchantments.THORNS, 1), 9, 1, 3, 15, 0.05F), VillagerType.DESERT),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_LEGGINGS, Enchantments.THORNS, 1), 11, 1, 3, 15, 0.05F), VillagerType.DESERT),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_CHESTPLATE, Enchantments.THORNS, 1), 13, 1, 3, 15, 0.05F), VillagerType.DESERT),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_BOOTS, Enchantments.PROTECTION, 1), 8, 1, 3, 15, 0.05F), VillagerType.PLAINS),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_HELMET, Enchantments.PROTECTION, 1), 9, 1, 3, 15, 0.05F), VillagerType.PLAINS),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_LEGGINGS, Enchantments.PROTECTION, 1), 11, 1, 3, 15, 0.05F), VillagerType.PLAINS),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_CHESTPLATE, Enchantments.PROTECTION, 1), 13, 1, 3, 15, 0.05F), VillagerType.PLAINS),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_BOOTS, Enchantments.BINDING_CURSE, 1), 2, 1, 3, 15, 0.05F), VillagerType.SAVANNA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_HELMET, Enchantments.BINDING_CURSE, 1), 3, 1, 3, 15, 0.05F), VillagerType.SAVANNA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_LEGGINGS, Enchantments.BINDING_CURSE, 1), 5, 1, 3, 15, 0.05F), VillagerType.SAVANNA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_CHESTPLATE, Enchantments.BINDING_CURSE, 1), 7, 1, 3, 15, 0.05F), VillagerType.SAVANNA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_BOOTS, Enchantments.FROST_WALKER, 1), 8, 1, 3, 15, 0.05F), VillagerType.SNOW),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.IRON_HELMET, Enchantments.AQUA_AFFINITY, 1), 9, 1, 3, 15, 0.05F), VillagerType.SNOW),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_BOOTS, Enchantments.UNBREAKING, 1), 8, 1, 3, 15, 0.05F), VillagerType.JUNGLE),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_HELMET, Enchantments.UNBREAKING, 1), 9, 1, 3, 15, 0.05F), VillagerType.JUNGLE),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_LEGGINGS, Enchantments.UNBREAKING, 1), 11, 1, 3, 15, 0.05F), VillagerType.JUNGLE),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_CHESTPLATE, Enchantments.UNBREAKING, 1), 13, 1, 3, 15, 0.05F), VillagerType.JUNGLE),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_BOOTS, Enchantments.MENDING, 1), 8, 1, 3, 15, 0.05F), VillagerType.SWAMP),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_HELMET, Enchantments.MENDING, 1), 9, 1, 3, 15, 0.05F), VillagerType.SWAMP),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_LEGGINGS, Enchantments.MENDING, 1), 11, 1, 3, 15, 0.05F), VillagerType.SWAMP),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_CHESTPLATE, Enchantments.MENDING, 1), 13, 1, 3, 15, 0.05F), VillagerType.SWAMP),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND_BOOTS, 1, 4, Items.DIAMOND_LEGGINGS, 1, 3, 15, 0.05F), VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND_LEGGINGS, 1, 4, Items.DIAMOND_CHESTPLATE, 1, 3, 15, 0.05F), VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND_HELMET, 1, 4, Items.DIAMOND_BOOTS, 1, 3, 15, 0.05F), VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND_CHESTPLATE, 1, 2, Items.DIAMOND_HELMET, 1, 3, 15, 0.05F), VillagerType.TAIGA)})
        .put(5, new TradeOffers.Factory[]{
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 4, 16, enchant(Items.DIAMOND_CHESTPLATE, Enchantments.THORNS, 1), 1, 3, 30, 0.05F), VillagerType.DESERT),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 3, 16, enchant(Items.DIAMOND_LEGGINGS, Enchantments.THORNS, 1), 1, 3, 30, 0.05F), VillagerType.DESERT),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 3, 16, enchant(Items.DIAMOND_LEGGINGS, Enchantments.PROTECTION, 1), 1, 3, 30, 0.05F), VillagerType.PLAINS),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 2, 12, enchant(Items.DIAMOND_BOOTS, Enchantments.PROTECTION, 1), 1, 3, 30, 0.05F), VillagerType.PLAINS),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 2, 6, enchant(Items.DIAMOND_HELMET, Enchantments.BINDING_CURSE, 1), 1, 3, 30, 0.05F), VillagerType.SAVANNA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 3, 8, enchant(Items.DIAMOND_CHESTPLATE, Enchantments.BINDING_CURSE, 1), 1, 3, 30, 0.05F), VillagerType.SAVANNA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 2, 12, enchant(Items.DIAMOND_BOOTS, Enchantments.FROST_WALKER, 1), 1, 3, 30, 0.05F), VillagerType.SNOW),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 3, 12, enchant(Items.DIAMOND_HELMET, Enchantments.AQUA_AFFINITY, 1), 1, 3, 30, 0.05F), VillagerType.SNOW),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_HELMET, Enchantments.PROJECTILE_PROTECTION, 1), 9, 1, 3, 30, 0.05F), VillagerType.JUNGLE),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_BOOTS, Enchantments.FEATHER_FALLING, 1), 8, 1, 3, 30, 0.05F), VillagerType.JUNGLE),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_HELMET, Enchantments.RESPIRATION, 1), 9, 1, 3, 30, 0.05F), VillagerType.SWAMP),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.SellItemFactory(enchant(Items.CHAINMAIL_BOOTS, Enchantments.DEPTH_STRIDER, 1), 8, 1, 3, 30, 0.05F), VillagerType.SWAMP),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 4, 18, enchant(Items.DIAMOND_CHESTPLATE, Enchantments.BLAST_PROTECTION, 1), 1, 3, 30, 0.05F), VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.ProcessItemFactory(Items.DIAMOND, 3, 18, enchant(Items.DIAMOND_LEGGINGS, Enchantments.BLAST_PROTECTION, 1), 1, 3, 30, 0.05F), VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.BuyItemFactory(Items.DIAMOND_BLOCK, 1, 12, 30, 42), VillagerType.TAIGA),
            TradeOffers.TypedWrapperFactory.of(new TradeOffers.BuyItemFactory(Items.IRON_BLOCK, 1, 12, 30, 4), VillagerType.DESERT, VillagerType.JUNGLE, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.SWAMP)}).build()),
        */

    /*private static TradeOffers.Factory createLibrarianTradeFactory(int experience) {
        return new WrapperFactory(ImmutableMap.builder()
        .put(VillagerType.DESERT, new TradeOffers.EnchantBookFactory(experience, new Enchantment[]{Enchantments.FIRE_PROTECTION, Enchantments.THORNS, Enchantments.INFINITY}))
        .put(VillagerType.JUNGLE, new TradeOffers.EnchantBookFactory(experience, new Enchantment[]{Enchantments.FEATHER_FALLING, Enchantments.PROJECTILE_PROTECTION, Enchantments.POWER}))
        .put(VillagerType.PLAINS, new TradeOffers.EnchantBookFactory(experience, new Enchantment[]{Enchantments.PUNCH, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS}))
        .put(VillagerType.SAVANNA, new TradeOffers.EnchantBookFactory(experience, new Enchantment[]{Enchantments.KNOCKBACK, Enchantments.BINDING_CURSE, Enchantments.SWEEPING}))
        .put(VillagerType.SNOW, new TradeOffers.EnchantBookFactory(experience, new Enchantment[]{Enchantments.AQUA_AFFINITY, Enchantments.LOOTING, Enchantments.FROST_WALKER}))
        .put(VillagerType.SWAMP, new TradeOffers.EnchantBookFactory(experience, new Enchantment[]{Enchantments.DEPTH_STRIDER, Enchantments.RESPIRATION, Enchantments.VANISHING_CURSE}))
        .put(VillagerType.TAIGA, new TradeOffers.EnchantBookFactory(experience, new Enchantment[]{Enchantments.BLAST_PROTECTION, Enchantments.FIRE_ASPECT, Enchantments.FLAME})).build());
    }
    private static TradeOffers.Factory createMasterLibrarianTradeFactory() {
        return new WrapperFactory(new Map<VillagerType, TradeOffers.Factory>() {
        new ImmutableMap.Builder.builder()
                .put(VillagerType.DESERT, new TradeOffers.EnchantBookFactory(30, 3, 3, new Enchantment[]{Enchantments.EFFICIENCY}))
                .put(VillagerType.JUNGLE, new TradeOffers.EnchantBookFactory(30, 2, 2, new Enchantment[]{Enchantments.UNBREAKING}))
                .put(VillagerType.PLAINS, new TradeOffers.EnchantBookFactory(30, 3, 3, new Enchantment[]{Enchantments.PROTECTION}))
                .put(VillagerType.SAVANNA, new TradeOffers.EnchantBookFactory(30, 3, 3, new Enchantment[]{Enchantments.SHARPNESS}))
                .put(VillagerType.SNOW, new TradeOffers.EnchantBookFactory(30, new Enchantment[]{Enchantments.SILK_TOUCH}))
                .put(VillagerType.SWAMP, new TradeOffers.EnchantBookFactory(30, new Enchantment[]{Enchantments.MENDING}))
                .put(VillagerType.TAIGA, new TradeOffers.EnchantBookFactory(30, 2, 2, new Enchantment[]{Enchantments.FORTUNE})).build());
        };
    }*/

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))

    private static void noEBookFirstTrade(CallbackInfo ci) {
        //temp biome/master books until I can work out how to make my own TradeOffers.TypedWrapperFactory for low and high level enchants
        TradeOffers.Factory biomeBook = Arrays.stream(REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.LIBRARIAN).get(2)).toList().get(1);
        TradeOffers.Factory masterBook = Arrays.stream(REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.LIBRARIAN).get(5)).toList().get(0);

        REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.LIBRARIAN).replace(1, new TradeOffers.Factory[]{new TradeOffers.BuyItemFactory(Items.PAPER, 24, 16, 2),
                new TradeOffers.BuyItemFactory(Items.BOOK, 4, 12, 2), new TradeOffers.SellItemFactory(Blocks.BOOKSHELF, 9, 1, 12, 1)});

        REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.LIBRARIAN).replace(2, new TradeOffers.Factory[]{/*createLibrarianTradeFactory(5)*/
                biomeBook, new TradeOffers.SellItemFactory(Items.LANTERN, 1, 1, 5)});

        REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.LIBRARIAN).replace(3, new TradeOffers.Factory[]{new TradeOffers.BuyItemFactory(Items.INK_SAC, 5, 12, 20),
                new TradeOffers.SellItemFactory(Items.GLASS, 1, 4, 10), new TradeOffers.SellItemFactory(Items.CLOCK, 5, 1, 15), new TradeOffers.SellItemFactory(Items.COMPASS, 4, 1, 15)});

        REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.LIBRARIAN).replace(4, new TradeOffers.Factory[]{
                new TradeOffers.EnchantBookFactory(10), new TradeOffers.BuyItemFactory(Items.WRITABLE_BOOK, 2, 12, 30)});

        REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.LIBRARIAN).replace(5, new TradeOffers.Factory[]{/*createMasterLibrarianTradeFactory()*/
                masterBook, new TradeOffers.SellItemFactory(Items.NAME_TAG, 20, 1, 30)});



        PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.TOOLSMITH).replace(3, new TradeOffers.Factory[]{
                new TradeOffers.BuyItemFactory(Items.FLINT, 30, 12, 20), new TradeOffers.SellEnchantedToolFactory(Items.IRON_AXE, 1, 3, 10, 0.2F),
                new TradeOffers.SellEnchantedToolFactory(Items.IRON_SHOVEL, 2, 3, 10, 0.2F), new TradeOffers.SellEnchantedToolFactory(Items.IRON_PICKAXE, 3, 3, 10, 0.2F),
                new TradeOffers.SellEnchantedToolFactory(Items.DIAMOND_HOE, 2, 3, 10, 0.2F)});

        PROFESSION_TO_LEVELED_TRADE.replace(VillagerProfession.LIBRARIAN, REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.LIBRARIAN));
        PROFESSION_TO_LEVELED_TRADE.replace(VillagerProfession.ARMORER, REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.ARMORER));
        PROFESSION_TO_LEVELED_TRADE.replace(VillagerProfession.CARTOGRAPHER, REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.CARTOGRAPHER));
    }
}
