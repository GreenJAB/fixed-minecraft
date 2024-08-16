package net.greenjab.fixedminecraft.registry

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

            entries.add(BlockRegistry.NETHERITE_ANVIL)
            entries.add(BlockRegistry.CHIPPED_NETHERITE_ANVIL)
            entries.add(BlockRegistry.DAMAGED_NETHERITE_ANVIL)

            entries.add(BlockRegistry.COPPER_RAIL)
            entries.add(BlockRegistry.EXPOSED_COPPER_RAIL)
            entries.add(BlockRegistry.WEATHERED_COPPER_RAIL)
            entries.add(BlockRegistry.OXIDIZED_COPPER_RAIL)
            entries.add(BlockRegistry.WAXED_COPPER_RAIL)
            entries.add(BlockRegistry.WAXED_EXPOSED_COPPER_RAIL)
            entries.add(BlockRegistry.WAXED_WEATHERED_COPPER_RAIL)
            entries.add(BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL)
        }
    }

    fun register() {
        Registries.ITEM_GROUP.register("fixed", FIXED)
    }
}
