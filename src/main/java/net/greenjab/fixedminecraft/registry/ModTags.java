package net.greenjab.fixedminecraft.registry;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModTags {
    public static final TagKey<EntityType<?>> VEHICLES = TagKey.create(Registries.ENTITY_TYPE, FixedMinecraft.id("vehicles"));
    public static final TagKey<Block> ORES = TagKey.create(Registries.BLOCK, FixedMinecraft.id("ores"));

    public static final TagKey<Biome> IS_FROZEN = TagKey.create(Registries.BIOME, FixedMinecraft.id("is_frozen"));

    public static final TagKey<Structure> LODESTONE_COMPASS = TagKey.create(Registries.STRUCTURE, FixedMinecraft.id("lodestone_compass"));
    public static final TagKey<Structure> ON_RUINED_PORTAL_MAPS = TagKey.create(Registries.STRUCTURE, FixedMinecraft.id("on_ruined_portal_maps"));
    public static final TagKey<Structure> ON_OUTPOST_MAPS = TagKey.create(Registries.STRUCTURE, FixedMinecraft.id("on_outpost_maps"));

    public static final TagKey<Item> STRINGTAG = TagKey.create(Registries.ITEM, FixedMinecraft.id("string"));
    public static final TagKey<Item> UNBREAKABLE = TagKey.create(Registries.ITEM, FixedMinecraft.id("unbreakable"));
    public static final TagKey<Item> STAINED_GLASS = TagKey.create(Registries.ITEM, FixedMinecraft.id("stained_glass"));
    public static final TagKey<Item> STAINED_GLASS_PANE = TagKey.create(Registries.ITEM, FixedMinecraft.id("stained_glass_pane"));
    public static final TagKey<Item> COPPER_ARMOR = TagKey.create(Registries.ITEM, FixedMinecraft.id("copper_armor"));


    public static final ResourceKey<TradeSet> WANDERING_TRADER_SPECIAL = TradeSets.resourceKey("wandering_trader/special");

    /*public static final TagKey<Enchantment> MINESHAFT_EBOOKS = enchant_of("loot/mineshaft");
    public static final TagKey<Enchantment> ANCIENT_CITY_EBOOKS = enchant_of("loot/ancient_city");
    public static final TagKey<Enchantment> BASTION_EBOOKS = enchant_of("loot/bastion");
    public static final TagKey<Enchantment> BURIED_TREASURE_EBOOKS = enchant_of("loot/buried_treasure");
    public static final TagKey<Enchantment> DESERT_PYRAMID_EBOOKS = enchant_of("loot/desert_pyramid");
    public static final TagKey<Enchantment> DUNGEON_EBOOKS = enchant_of("loot/dungeon");
    public static final TagKey<Enchantment> END_CITY_EBOOKS = enchant_of("loot/end_city");
    public static final TagKey<Enchantment> IGLOO_EBOOKS = enchant_of("loot/igloo");
    public static final TagKey<Enchantment> JUNGLE_TEMPLE_EBOOKS = enchant_of("loot/jungle_temple");
    public static final TagKey<Enchantment> MANSION_EBOOKS = enchant_of("loot/mansion");
    public static final TagKey<Enchantment> NETHER_FORTRESS_EBOOKS = enchant_of("loot/nether_fortress");
    public static final TagKey<Enchantment> OCEAN_RUINS_EBOOKS = enchant_of("loot/ocean_ruins");
    public static final TagKey<Enchantment> PILLAGER_OUTPOST_EBOOKS = enchant_of("loot/pillager_outpost");
    public static final TagKey<Enchantment> RUINED_PORTAL_EBOOKS = enchant_of("loot/ruined_portal");
    public static final TagKey<Enchantment> SHIPWRECK_EBOOKS = enchant_of("loot/shipwreck");
    public static final TagKey<Enchantment> STRONGHOLD_EBOOKS = enchant_of("loot/stronghold");
    public static final TagKey<Enchantment> SWAMP_HUT_EBOOKS = enchant_of("loot/swamp_hut");
    public static final TagKey<Enchantment> TRIAL_CHAMBER_EBOOKS = enchant_of("loot/trial_chamber");
    public static final TagKey<Enchantment> TRAIL_RUINS_EBOOKS = enchant_of("loot/trail_ruins");
    public static final TagKey<Enchantment> FISHING_EBOOKS = enchant_of("loot/fishing");*/

    private static TagKey<Enchantment> enchant_of(String id)  {
        return TagKey.create(Registries.ENCHANTMENT, FixedMinecraft.id(id));
    }
}
