package net.greenjab.fixedminecraft.registry

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.Text

@Suppress("MemberVisibilityCanBePrivate")
object ItemGroupRegistry {
    val FIXED = itemGroup(Text.translatable("itemgroup.fixed")) {
        icon { ItemStack(ItemRegistry.DRAGON_FIREWORK_ROCKET) }
        entries { _: ItemGroup.DisplayContext, entries: ItemGroup.Entries ->
            entries.add(ItemRegistry.DRAGON_FIREWORK_ROCKET)
            entries.add(ItemRegistry.MAP_BOOK)
            entries.add(ItemRegistry.NETHERITE_HORSE_ARMOR)

            entries.add(ItemRegistry.BROKEN_TOTEM)
            entries.add(ItemRegistry.ECHO_TOTEM)
            entries.add(ItemRegistry.ECHO_FRUIT)

            entries.add(ItemRegistry.NETHERITE_ANVIL)
            entries.add(ItemRegistry.CHIPPED_NETHERITE_ANVIL)
            entries.add(ItemRegistry.DAMAGED_NETHERITE_ANVIL)

            entries.add(ItemRegistry.COPPER_RAIL)
            entries.add(ItemRegistry.EXPOSED_COPPER_RAIL)
            entries.add(ItemRegistry.WEATHERED_COPPER_RAIL)
            entries.add(ItemRegistry.OXIDIZED_COPPER_RAIL)
            entries.add(ItemRegistry.WAXED_COPPER_RAIL)
            entries.add(ItemRegistry.WAXED_EXPOSED_COPPER_RAIL)
            entries.add(ItemRegistry.WAXED_WEATHERED_COPPER_RAIL)
            entries.add(ItemRegistry.WAXED_OXIDIZED_COPPER_RAIL)
        }
    }

    fun register() {
        Registries.ITEM_GROUP.register("fixed", FIXED)
    }
}
