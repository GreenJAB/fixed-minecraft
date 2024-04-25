package net.greenjab.fixedminecraft.items

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.greenjab.fixedminecraft.items.map_book.MapBookItem
import net.greenjab.fixedminecraft.util.identifierOf
import net.greenjab.fixedminecraft.util.register
import net.minecraft.item.FireworkRocketItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries

object ItemRegistry {
    val DRAGON_FIREWORK_ROCKET: Item = FireworkRocketItem(Item.Settings())
    val MAP_BOOK: Item = MapBookItem(FabricItemSettings().maxCount(1))

    fun register() {
        Registries.ITEM.register(identifierOf("dragon_firework_rocket"), DRAGON_FIREWORK_ROCKET)
        Registries.ITEM.register(identifierOf("map_book"), MAP_BOOK)
    }
}
