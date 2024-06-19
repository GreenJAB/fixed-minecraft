package net.greenjab.fixedminecraft.items

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.greenjab.fixedminecraft.blocks.BlockRegistry
import net.greenjab.fixedminecraft.util.identifierOf
import net.greenjab.fixedminecraft.util.register
import net.minecraft.block.Blocks
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.item.BoatItem
import net.minecraft.item.FireworkRocketItem
import net.minecraft.item.HangingSignItem
import net.minecraft.item.Item
import net.minecraft.item.SignItem
import net.minecraft.registry.Registries

object ItemRegistry {
    val DRAGON_FIREWORK_ROCKET: Item = FireworkRocketItem(Item.Settings())
    val MAP_BOOK: Item = Item(FabricItemSettings())

    val AZALEA_SIGN: Item = SignItem((Item.Settings()).maxCount(16), BlockRegistry.AZALEA_SIGN, BlockRegistry.AZALEA_WALL_SIGN)
    val AZALEA_HANGING_SIGN: Item = HangingSignItem(BlockRegistry.AZALEA_HANGING_SIGN, BlockRegistry.AZALEA_WALL_HANGING_SIGN, (Item.Settings()).maxCount(16))
    val AZALEA_BOAT: Item = BoatItem(false, BoatEntity.Type.ACACIA, (Item.Settings()).maxCount(1))
    val AZALEA_CHEST_BOAT: Item = BoatItem(true, BoatEntity.Type.ACACIA, (Item.Settings()).maxCount(1))

    fun register() {
        Registries.ITEM.register(identifierOf("dragon_firework_rocket"), DRAGON_FIREWORK_ROCKET)
        Registries.ITEM.register(identifierOf("map_book"), MAP_BOOK)

        Registries.ITEM.register(identifierOf("azalea_sign"), AZALEA_SIGN)
        Registries.ITEM.register(identifierOf("azalea_hanging_sign"), AZALEA_HANGING_SIGN)
        Registries.ITEM.register(identifierOf("azalea_boat"), AZALEA_BOAT)
        Registries.ITEM.register(identifierOf("azalea_chest_boat"), AZALEA_CHEST_BOAT)
    }
}
