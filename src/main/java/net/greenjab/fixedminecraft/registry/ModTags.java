package net.greenjab.fixedminecraft.registry;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public class ModTags {
    public static final TagKey<EntityType<?>> VEHICLES = TagKey.of(RegistryKeys.ENTITY_TYPE, FixedMinecraft.INSTANCE.id("vehicles"));
    public static final TagKey<Block> ORES = TagKey.of(RegistryKeys.BLOCK, FixedMinecraft.INSTANCE.id("ores"));

    public static final TagKey<Enchantment> DESERT_TRADES = enchant_of("trades/desert");
    public static final TagKey<Enchantment> JUNGLE_TRADES = enchant_of("trades/jungle");
    public static final TagKey<Enchantment> PLAINS_TRADES = enchant_of("trades/plains");
    public static final TagKey<Enchantment> SAVANNA_TRADES = enchant_of("trades/savanna");
    public static final TagKey<Enchantment> SNOW_TRADES = enchant_of("trades/snow");
    public static final TagKey<Enchantment> SWAMP_TRADES = enchant_of("trades/swamp");
    public static final TagKey<Enchantment> TAIGA_TRADES = enchant_of("trades/taiga");
    public static final TagKey<Enchantment> ANY_TRADES = enchant_of("trades/any");
    public static final TagKey<Enchantment> FISHING_TRADES = enchant_of("trades/fishing");

    public static final TagKey<Biome> IS_PALE_GARDEN = TagKey.of(RegistryKeys.BIOME, FixedMinecraft.INSTANCE.id("is_pale_garden"));

    public static final TagKey<Item> STRINGTAG = TagKey.of(RegistryKeys.ITEM, FixedMinecraft.INSTANCE.id("string"));

    private static TagKey<Enchantment> enchant_of(String id)  {
        return TagKey.of(RegistryKeys.ENCHANTMENT, FixedMinecraft.INSTANCE.id(id));
    }
}
