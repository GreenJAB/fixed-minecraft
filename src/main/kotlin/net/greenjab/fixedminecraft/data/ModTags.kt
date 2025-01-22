package net.greenjab.fixedminecraft.data

import net.greenjab.fixedminecraft.FixedMinecraft
import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object ModTags {

    val VEHICLES: TagKey<EntityType<*>> = TagKey.of(RegistryKeys.ENTITY_TYPE, FixedMinecraft.id("vehicles"))
    val ORES: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, FixedMinecraft.id("ores"))

    val DESERT_TRADES: TagKey<Enchantment> = enchant_of("trades/desert")
    val JUNGLE_TRADES: TagKey<Enchantment> = enchant_of("trades/jungle")
    val PLAINS_TRADES: TagKey<Enchantment> = enchant_of("trades/plains")
    val SAVANNA_TRADES: TagKey<Enchantment> = enchant_of("trades/savanna")
    val SNOW_TRADES: TagKey<Enchantment> = enchant_of("trades/snow")
    val SWAMP_TRADES: TagKey<Enchantment> = enchant_of("trades/swamp")
    val TAIGA_TRADES: TagKey<Enchantment> = enchant_of("trades/taiga")
    val ANY_TRADES: TagKey<Enchantment> = enchant_of("trades/any")
    val FISHING_TRADES: TagKey<Enchantment> = enchant_of("trades/fishing")

    private fun enchant_of(id: String): TagKey<Enchantment> {
        return TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.ofVanilla(id))
    }

}
