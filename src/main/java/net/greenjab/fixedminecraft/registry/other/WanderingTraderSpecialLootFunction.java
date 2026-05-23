package net.greenjab.fixedminecraft.registry.other;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class WanderingTraderSpecialLootFunction extends LootItemConditionalFunction {
    public static final MapCodec<WanderingTraderSpecialLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            i -> commonFields(i)
                    .apply(i, WanderingTraderSpecialLootFunction::new)
    );

    private WanderingTraderSpecialLootFunction(final List<LootItemCondition> predicates) {
        super(predicates);
    }

    @Override
    public @NonNull MapCodec<WanderingTraderSpecialLootFunction> codec() {
        return CODEC;
    }

    @Override
    public @NonNull ItemStack run(final @NonNull ItemStack itemStack, final @NonNull LootContext context) {
        int i = (int)(Math.random()*5);
        return switch (i) {
            case 0 -> createMusicDiscStack();
            case 1 -> createSherdStack();
            case 2 -> createTrimStack();
            case 3 -> createMobHeadStack(context);
            default -> createBiomeMapStack(context);
        };
    }


    @Unique
    private ItemStack createMusicDiscStack() {
        Item[] discs = {Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR,
                Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD,
                Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT, Items.MUSIC_DISC_PIGSTEP, Items.MUSIC_DISC_OTHERSIDE, Items.MUSIC_DISC_5,
                Items.MUSIC_DISC_RELIC, Items.MUSIC_DISC_CREATOR, Items.MUSIC_DISC_CREATOR_MUSIC_BOX, Items.MUSIC_DISC_PRECIPICE};
        return discs[(int)(Math.random()*discs.length)].getDefaultInstance();
    }

    @Unique
    private ItemStack createSherdStack() {
        Item[] sherds = {Items.ANGLER_POTTERY_SHERD, Items.ARCHER_POTTERY_SHERD, Items.ARMS_UP_POTTERY_SHERD, Items.BLADE_POTTERY_SHERD,
                Items.BREWER_POTTERY_SHERD, Items.BURN_POTTERY_SHERD, Items.DANGER_POTTERY_SHERD, Items.EXPLORER_POTTERY_SHERD,
                Items.FLOW_POTTERY_SHERD, Items.FRIEND_POTTERY_SHERD, Items.GUSTER_POTTERY_SHERD, Items.HEART_POTTERY_SHERD,
                Items.HEARTBREAK_POTTERY_SHERD, Items.HOWL_POTTERY_SHERD, Items.MINER_POTTERY_SHERD, Items.MOURNER_POTTERY_SHERD,
                Items.PLENTY_POTTERY_SHERD, Items.PRIZE_POTTERY_SHERD, Items.SCRAPE_POTTERY_SHERD, Items.SHEAF_POTTERY_SHERD,
                Items.SHELTER_POTTERY_SHERD, Items.SKULL_POTTERY_SHERD, Items.SNORT_POTTERY_SHERD};
        return sherds[(int)(Math.random()*sherds.length)].getDefaultInstance();
    }

    @Unique
    private ItemStack createTrimStack() {
        Item[] trims = {Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE};
        return trims[(int)(Math.random()*trims.length)].getDefaultInstance();
    }

    @Unique
    private ItemStack createMobHeadStack(LootContext context) {
        Item[] heads = {Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CREEPER_HEAD, Items.WITHER_SKELETON_SKULL, Items.PIGLIN_HEAD, Items.PLAYER_HEAD};
        ItemStack head = heads[(int)(Math.random()*heads.length)].getDefaultInstance();
        if (head.is(Items.PLAYER_HEAD)) {

            MinecraftServer minecraftServer = context.getLevel().getServer();
            ProfileResolver lv = minecraftServer.services().profileResolver();
            Optional<GameProfile> optional;
            int who = minecraftServer.overworld().getRandom().nextInt(3);
            switch (who) {
                case 0:
                    //mod maker
                    optional = lv.fetchByName("green_jab");
                    head.set(DataComponents.PROFILE, ResolvableProfile.createResolved(optional.get()));
                    break;
                case 1:
                    //patreon
                    String[] names = {"Rellati"};
                    optional = lv.fetchByName(names[(int)(Math.random()*names.length)]);
                    head.set(DataComponents.PROFILE, ResolvableProfile.createResolved(optional.get()));
                    break;
                default:
                    //blank head
            }

        }
        return head;
    }

    @Unique
    private ItemStack createBiomeMapStack(LootContext context) {
        ServerLevel level = context.getLevel();
        int map = level.getRandom().nextInt(6);
        final ResourceKey<Biome> biomeSearch = switch (map) {
            case 0 -> Biomes.MUSHROOM_FIELDS;
            case 1 -> Biomes.CHERRY_GROVE;
            case 2 -> Biomes.ICE_SPIKES;
            case 3 -> Biomes.BADLANDS;
            case 4 -> Biomes.WARM_OCEAN;
            case 5 -> Biomes.PALE_GARDEN;
            default -> Biomes.FOREST;
        };

        Predicate<Holder<Biome>> predicate =entry -> entry.is(biomeSearch);

        Pair<BlockPos, Holder<Biome>> pair = level.findClosestBiome3d(predicate, BlockPos.containing(context.getOptionalParameter(LootContextParams.ORIGIN)), 6400, 32, 64);
        if (pair != null) {
            BlockPos blockPos = pair.getFirst();
            ItemStack itemStack = MapItem.create(level, blockPos.getX(), blockPos.getZ(), (byte) 2, true, true);
            MapItem.renderBiomePreviewMap(level, itemStack);

            String[] names = {"mushroom_fields", "cherry_grove", "ice_spikes", "badlands", "warm_ocean", "pale_garden"};
            Component name = Component.translatable("filled_map.explorer", Component.translatable("biome.minecraft." + names[map]));
            int[] colour = {7412448, 16751570, 4639231, 16725801, 1938431, 10856879};

            itemStack.set(DataComponents.ITEM_NAME, name);
            MapItemSavedData m = MapItem.getSavedData(itemStack, level);
            assert m != null;
            m.toggleBanner(level, new BlockPos(blockPos.getX(), -1000 - map, blockPos.getZ()));
            itemStack.set(DataComponents.MAP_COLOR, new MapItemColor(colour[map]));

            return itemStack;
        }
        return Items.MAP.getDefaultInstance();
    }

}
