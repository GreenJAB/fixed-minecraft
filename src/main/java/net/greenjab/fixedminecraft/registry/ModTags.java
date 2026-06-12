package net.greenjab.fixedminecraft.registry;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
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
}
