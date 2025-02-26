package net.greenjab.fixedminecraft.mixin.villager;

import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin {
    @Redirect(method = "fillRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/featuretoggle/FeatureSet;contains(Lnet/minecraft/resource/featuretoggle/FeatureFlag;)Z"))
    private boolean rebalancedWanderingTrader(FeatureSet instance, FeatureFlag feature) {
        return true;
    }

    @ModifyVariable(method = "fillRebalancedRecipes", at = @At("STORE"), ordinal = 0)
    private Iterator<Pair<TradeOffers.Factory[], Integer>> newTrades(Iterator<Pair<TradeOffers.Factory[], Integer>> iter){
        List<Pair<TradeOffers.Factory[], Integer>> list = List.of(
                Pair.of(new TradeOffers.Factory[]{
                        new TradeOffers.BuyItemFactory(createPotion(), 1, 1, 1),
                        new TradeOffers.BuyItemFactory(Items.WATER_BUCKET, 1, 1, 1, 2),
                        new TradeOffers.BuyItemFactory(Items.MILK_BUCKET, 1, 1, 1, 2),
                        new TradeOffers.BuyItemFactory(Items.FERMENTED_SPIDER_EYE, 1, 1, 1, 3),
                        new TradeOffers.BuyItemFactory(Items.BAKED_POTATO, 4, 1, 1),
                        new TradeOffers.BuyItemFactory(Items.HAY_BLOCK, 2, 1, 1),

                        new TradeOffers.BuyItemFactory(Items.GOLDEN_CARROT, 2, 1, 1),
                        new TradeOffers.BuyItemFactory(Items.PUMPKIN_PIE, 2, 1, 1),
                        new TradeOffers.BuyItemFactory(Items.BEETROOT_SOUP, 1, 1, 1),
                        new TradeOffers.BuyItemFactory(Items.COMPASS, 1, 1, 1),
                        new TradeOffers.BuyItemFactory(Items.LEAD, 2, 1, 1),
                        new TradeOffers.BuyItemFactory(Items.COOKIE, 2, 1, 1),
                }, 3+(int)(Math.random()*2)),
                Pair.of(new TradeOffers.Factory[]{
                        new TradeOffers.SellItemFactory(Items.PACKED_ICE, 1, 1, 6, 1),
                        new TradeOffers.SellItemFactory(Items.BLUE_ICE, 6, 1, 6, 1),
                        new TradeOffers.SellItemFactory(Items.GUNPOWDER, 1, 4, 2, 1),
                        new TradeOffers.SellItemFactory(Items.PODZOL, 3, 3, 6, 1),
                        new TradeOffers.SellItemFactory(Blocks.ACACIA_LOG, 1, 8, 4, 1),
                        new TradeOffers.SellItemFactory(Blocks.BIRCH_LOG, 1, 8, 4, 1),
                        new TradeOffers.SellItemFactory(Blocks.DARK_OAK_LOG, 1, 8, 4, 1),
                        new TradeOffers.SellItemFactory(Blocks.JUNGLE_LOG, 1, 8, 4, 1),
                        new TradeOffers.SellItemFactory(Blocks.OAK_LOG, 1, 8, 4, 1),
                        new TradeOffers.SellItemFactory(Blocks.SPRUCE_LOG, 1, 8, 4, 1),
                        new TradeOffers.SellItemFactory(Blocks.CHERRY_LOG, 1, 8, 4, 1),
                        new TradeOffers.SellEnchantedToolFactory(Items.IRON_PICKAXE, 1, 1, 1, 0.2F),
                        new TradeOffers.SellItemFactory(createPotionStack(), 5, 1, 1, 1),
                        new TradeOffers.SellItemFactory(Items.NAUTILUS_SHELL, 5, 1, 5, 1)
                }, 2+(int)(Math.random()*2)),
                Pair.of(new TradeOffers.Factory[]{
                        new TradeOffers.SellItemFactory(Items.TROPICAL_FISH_BUCKET, 3, 1, 4, 1),
                        new TradeOffers.SellItemFactory(Items.PUFFERFISH_BUCKET, 3, 1, 4, 1),
                        new TradeOffers.SellItemFactory(Items.SEA_PICKLE, 2, 1, 5, 1),
                        new TradeOffers.SellItemFactory(Items.SLIME_BALL, 4, 1, 5, 1),
                        new TradeOffers.SellItemFactory(Items.GLOWSTONE, 2, 1, 5, 1),
                        new TradeOffers.SellItemFactory(Items.FERN, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.SUGAR_CANE, 1, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.PUMPKIN, 1, 1, 4, 1),
                        new TradeOffers.SellItemFactory(Items.KELP, 3, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.CACTUS, 3, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.DANDELION, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.POPPY, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.BLUE_ORCHID, 1, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.ALLIUM, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.AZURE_BLUET, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.RED_TULIP, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.ORANGE_TULIP, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.WHITE_TULIP, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.PINK_TULIP, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.OXEYE_DAISY, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.CORNFLOWER, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1),
                        new TradeOffers.SellItemFactory(Items.WHEAT_SEEDS, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.BEETROOT_SEEDS, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.PUMPKIN_SEEDS, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.MELON_SEEDS, 1, 1, 12, 1),
                        new TradeOffers.SellItemFactory(Items.ACACIA_SAPLING, 5, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.BIRCH_SAPLING, 5, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.DARK_OAK_SAPLING, 5, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.JUNGLE_SAPLING, 5, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.OAK_SAPLING, 5, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.SPRUCE_SAPLING, 5, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.CHERRY_SAPLING, 5, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.MANGROVE_PROPAGULE, 5, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.RED_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.WHITE_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.BLUE_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.PINK_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.BLACK_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.GREEN_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.LIGHT_GRAY_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.MAGENTA_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.YELLOW_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.GRAY_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.PURPLE_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.LIGHT_BLUE_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.LIME_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.ORANGE_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.BROWN_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.CYAN_DYE, 1, 3, 12, 1),
                        new TradeOffers.SellItemFactory(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1),
                        new TradeOffers.SellItemFactory(Items.VINE, 1, 3, 4, 1),
                        new TradeOffers.SellItemFactory(Items.BROWN_MUSHROOM, 1, 3, 4, 1),
                        new TradeOffers.SellItemFactory(Items.RED_MUSHROOM, 1, 3, 4, 1),
                        new TradeOffers.SellItemFactory(Items.LILY_PAD, 1, 5, 2, 1),
                        new TradeOffers.SellItemFactory(Items.SMALL_DRIPLEAF, 1, 2, 5, 1),
                        new TradeOffers.SellItemFactory(Items.SAND, 1, 8, 8, 1),
                        new TradeOffers.SellItemFactory(Items.RED_SAND, 1, 4, 6, 1),
                        new TradeOffers.SellItemFactory(Items.POINTED_DRIPSTONE, 1, 2, 5, 1),
                        new TradeOffers.SellItemFactory(Items.ROOTED_DIRT, 1, 2, 5, 1),
                        new TradeOffers.SellItemFactory(Items.MOSS_BLOCK, 1, 2, 5, 1)
                }, 2+(int)(Math.random()*2)),
                Pair.of(new TradeOffers.Factory[]{
                        new TradeOffers.SellItemFactory(createSpecialItem(), 10, 1, 1, 1)
                }, 1)

        );
        return list.iterator();
    }

    @Unique
    private ItemStack createSpecialItem() {
        int i = (int)(Math.random()*5);
        return switch (i) {
            case 0 -> createMusicDiscStack();
            case 1 -> createSherdStack();
            case 2 -> createTrimStack();
            case 3 -> createMobHeadStack();
            default -> createBiomeMapStack();
        };
    }

    @Unique
    private TradedItem createPotion() {
        return new TradedItem(Items.POTION)
                .withComponents(/* method_57312 */ builder -> builder.add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.WATER)));
    }

    @Unique
    private ItemStack createPotionStack() {
        return PotionContentsComponent.createStack(Items.POTION, Potions.LONG_INVISIBILITY);
    }

    @Unique
    private ItemStack createMusicDiscStack() {
        Item[] discs = {Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR,
                Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD,
                Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT, Items.MUSIC_DISC_OTHERSIDE, Items.MUSIC_DISC_RELIC, Items.MUSIC_DISC_5,
                Items.MUSIC_DISC_PIGSTEP};
        return discs[(int)(Math.random()*discs.length)].getDefaultStack();
    }

    @Unique
    private ItemStack createSherdStack() {
        Item[] sherds = {Items.ANGLER_POTTERY_SHERD, Items.ARCHER_POTTERY_SHERD, Items.ARMS_UP_POTTERY_SHERD, Items.BLADE_POTTERY_SHERD,
                Items.BREWER_POTTERY_SHERD, Items.BURN_POTTERY_SHERD, Items.DANGER_POTTERY_SHERD, Items.EXPLORER_POTTERY_SHERD,
                Items.FRIEND_POTTERY_SHERD, Items.HEART_POTTERY_SHERD, Items.HEARTBREAK_POTTERY_SHERD, Items.HOWL_POTTERY_SHERD,
                Items.MINER_POTTERY_SHERD, Items.MOURNER_POTTERY_SHERD, Items.PLENTY_POTTERY_SHERD, Items.PRIZE_POTTERY_SHERD,
                Items.SHEAF_POTTERY_SHERD, Items.SHELTER_POTTERY_SHERD, Items.SKULL_POTTERY_SHERD, Items.SNORT_POTTERY_SHERD};
        return sherds[(int)(Math.random()*sherds.length)].getDefaultStack();
    }

    @Unique
    private ItemStack createTrimStack() {
        Item[] trims = {Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE};
        return trims[(int)(Math.random()*trims.length)].getDefaultStack();
    }

    @Unique
    private ItemStack createMobHeadStack() {
        Item[] heads = {Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CREEPER_HEAD, Items.WITHER_SKELETON_SKULL, Items.PIGLIN_HEAD, Items.PLAYER_HEAD};
        ItemStack head = heads[(int)(Math.random()*heads.length)].getDefaultStack();
        if (head.isOf(Items.PLAYER_HEAD)) {
            WanderingTraderEntity WTE = (WanderingTraderEntity)(Object)this;
            PlayerEntity playerEntity = WTE.getEntityWorld().getClosestPlayer(WTE, 100);
            if (playerEntity != null) {
                head.set(DataComponentTypes.PROFILE, new ProfileComponent(playerEntity.getGameProfile()));
            }
        }
        return head;
    }

    @Unique
    private ItemStack createBiomeMapStack() {
        WanderingTraderEntity WTE = (WanderingTraderEntity)(Object)this;
            List<RegistryKey<Biome>> biomes = List.of();
            Set<RegistryKey<Biome>> var10001 = Set.copyOf(biomes);
            Objects.requireNonNull(var10001);
            Predicate<RegistryEntry<Biome>> predicate = var10001::contains;

            World world = WTE.getWorld();
            if (world instanceof ServerWorld serverWorld) {

                int map = serverWorld.random.nextInt(6);
                switch (map) {
                    case 0:
                        CustomData.biomeSearch = BiomeKeys.MUSHROOM_FIELDS;
                        break;
                    case 1:
                        CustomData.biomeSearch = BiomeKeys.CHERRY_GROVE;
                        break;
                    case 2:
                        CustomData.biomeSearch = BiomeKeys.ICE_SPIKES;
                        break;
                    case 3:
                        CustomData.biomeSearch = BiomeKeys.BADLANDS;
                        break;
                    case 4:
                        CustomData.biomeSearch = BiomeKeys.WARM_OCEAN;
                        break;
                    case 5:
                        CustomData.biomeSearch = BiomeKeys.PALE_GARDEN;
                        break;
                    default:
                        CustomData.biomeSearch = BiomeKeys.FOREST;
                        break;
                }

                com.mojang.datafixers.util.Pair<BlockPos, RegistryEntry<Biome>> pair = serverWorld.locateBiome(predicate.negate(), WTE.getBlockPos(), 6400, 32, 64);
                if (pair != null) {
                    BlockPos blockPos = pair.getFirst();
                    ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), (byte) 2, true, true);
                    FilledMapItem.fillExplorationMap(serverWorld, itemStack);

                    String[] name = {"Mushroom Fields", "Cherry Grove", "Ice Spikes", "Badlands", "Warm Ocean", "Pale Garden"};
                    int[] colour = {7412448, 16751570, 4639231, 16725801, 1938431, 10856879};

                    itemStack.set(DataComponentTypes.ITEM_NAME, Text.of(name[map] + " Explorer Map"));
                    MapState m = FilledMapItem.getMapState(itemStack, serverWorld);
                    assert m != null;
                    m.addBanner(serverWorld, new BlockPos(blockPos.getX(), -1000 - map, blockPos.getZ()));
                    itemStack.set(DataComponentTypes.MAP_COLOR, new MapColorComponent(colour[map]));

                    return itemStack;
                }
            }
        return Items.MAP.getDefaultStack();
    }
}
