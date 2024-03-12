package net.greenjab.fixedminecraft.items

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.greenjab.fixedminecraft.util.identifierOf
import net.greenjab.fixedminecraft.util.register
import net.minecraft.item.FireworkRocketItem
import net.minecraft.item.HorseArmorItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries.ITEM
import net.minecraft.util.Rarity

object ItemRegistry {
    val DRAGON_FIREWORK_ROCKET: Item = FireworkRocketItem(Item.Settings())
    val MAP_BOOK: Item = Item(FabricItemSettings())
    val NETHERITE_HORSE_ARMOR = HorseArmorItem(15, "netherite", FabricItemSettings().maxCount(1).rarity(Rarity.RARE).fireproof())

    fun register() {
        ITEM.register(identifierOf("dragon_firework_rocket"), DRAGON_FIREWORK_ROCKET)
        ITEM.register(identifierOf("map_book"), MAP_BOOK)
        ITEM.register(identifierOf("netherite_horse_armor"), NETHERITE_HORSE_ARMOR)
    }
}
