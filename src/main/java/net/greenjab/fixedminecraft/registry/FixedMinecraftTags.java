package net.greenjab.fixedminecraft.registry;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public interface FixedMinecraftTags {
    TagKey<EntityType<?>> VEHICLES = TagKey.create(Registries.ENTITY_TYPE, FixedMinecraft.id("vehicles"));
    TagKey<Block> ORES = TagKey.create(Registries.BLOCK, FixedMinecraft.id("ores"));

    TagKey<Biome> IS_FROZEN = TagKey.create(Registries.BIOME, FixedMinecraft.id("is_frozen"));

    TagKey<Structure> LODESTONE_COMPASS = TagKey.create(Registries.STRUCTURE, FixedMinecraft.id("lodestone_compass"));
    TagKey<Structure> ON_OUTPOST_MAPS = TagKey.create(Registries.STRUCTURE, FixedMinecraft.id("on_outpost_maps"));

    TagKey<Item> STRING = TagKey.create(Registries.ITEM, FixedMinecraft.id("string"));
    TagKey<Item> BREEZE_ROD = TagKey.create(Registries.ITEM, FixedMinecraft.id("breeze_rod"));
    TagKey<Item> UNBREAKABLE = TagKey.create(Registries.ITEM, FixedMinecraft.id("unbreakable"));
    TagKey<Item> PARTIAL_KEEP_INVENTORY = TagKey.create(Registries.ITEM, FixedMinecraft.id("partial_keep_inventory"));
    TagKey<Item> STAINED_GLASS = TagKey.create(Registries.ITEM, FixedMinecraft.id("stained_glass"));
    TagKey<Item> STAINED_GLASS_PANE = TagKey.create(Registries.ITEM, FixedMinecraft.id("stained_glass_pane"));
    TagKey<Item> COPPER_ARMOR = TagKey.create(Registries.ITEM, FixedMinecraft.id("copper_armor"));
    TagKey<Item> VANILLA_NAUTILUS_ARMOR = TagKey.create(Registries.ITEM, FixedMinecraft.id("vanilla_nautilus_armor"));

    ResourceKey<TradeSet> WANDERING_TRADER_SPECIAL = TradeSets.resourceKey("wandering_trader/special");

    TagKey<Enchantment> ABANDONED_MINESHAFT_EBOOKS = enchant_of("chest/abandoned_mineshaft");
    TagKey<Enchantment> ANCIENT_CITY_EBOOKS = enchant_of("chest/ancient_city");
    TagKey<Enchantment> BASTION_TREASURE_EBOOKS = enchant_of("chest/bastion_treasure");
    TagKey<Enchantment> BURIED_TREASURE_EBOOKS = enchant_of("chest/buried_treasure");
    TagKey<Enchantment> DESERT_PYRAMID_EBOOKS = enchant_of("chest/desert_pyramid");
    TagKey<Enchantment> END_CITY_TREASURE_EBOOKS = enchant_of("chest/end_city_treasure");
    TagKey<Enchantment> IGLOO_CHEST_EBOOKS = enchant_of("chest/igloo_chest");
    TagKey<Enchantment> JUNGLE_TEMPLE_EBOOKS = enchant_of("chest/jungle_temple");
    TagKey<Enchantment> NETHER_BRIDGE_EBOOKS = enchant_of("chest/nether_bridge");
    TagKey<Enchantment> PILLAGER_OUTPOST_EBOOKS = enchant_of("chest/pillager_outpost");
    TagKey<Enchantment> RUINED_PORTAL_EBOOKS = enchant_of("chest/ruined_portal");
    TagKey<Enchantment> SHIPWRECK_TREASURE_EBOOKS = enchant_of("chest/shipwreck_treasure");
    TagKey<Enchantment> SIMPLE_DUNGEON_EBOOKS = enchant_of("chest/simple_dungeon");
    TagKey<Enchantment> STRONGHOLD_LIBRARY_EBOOKS = enchant_of("chest/stronghold_library");
    TagKey<Enchantment> SWAMP_HUT_EBOOKS = enchant_of("chest/swamp_hut");
    TagKey<Enchantment> UNDERWATER_RUIN_BIG_EBOOKS = enchant_of("chest/underwater_ruin_big");
    TagKey<Enchantment> WOODLAND_MANSION_EBOOKS = enchant_of("chest/woodland_mansion");

    TagKey<Enchantment> TRIAL_CHAMBER_EBOOKS = enchant_of("other/trial_chamber");
    TagKey<Enchantment> TRAIL_RUINS_EBOOKS = enchant_of("other/trail_ruins");
    TagKey<Enchantment> FISHING_TREASURE_EBOOKS = enchant_of("other/fishing_treasure");

    TagKey<DamageType> IGNORE_CHAINMAIL_PROTECTION_INCREASE = TagKey.create(Registries.DAMAGE_TYPE, FixedMinecraft.id("ignore_chainmail_protection_increase"));

    private static TagKey<Enchantment> enchant_of(String id)  {
        return TagKey.create(Registries.ENCHANTMENT, FixedMinecraft.id(id));
    }
}
