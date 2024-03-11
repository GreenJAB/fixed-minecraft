package net.greenjab.fixedminecraft.items

import net.greenjab.fixedminecraft.blocks.BlockRegistry
import net.greenjab.fixedminecraft.util.identifierOf
import net.greenjab.fixedminecraft.util.itemGroup
import net.greenjab.fixedminecraft.util.register
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.Text

object ItemGroupRegistry {
    val FIXED = itemGroup(Text.translatable("itemgroup.fixed")) {
        icon { ItemStack(ItemRegistry.DRAGON_FIREWORK_ROCKET) }
        entries { _: ItemGroup.DisplayContext, entries: ItemGroup.Entries ->
            entries.add(ItemRegistry.DRAGON_FIREWORK_ROCKET)
            entries.add(ItemRegistry.MAP_BOOK)
            entries.add(ItemRegistry.NETHERITE_HORSE_ARMOR)
            entries.add(BlockRegistry.NETHERITE_ANVIL)
            entries.add(BlockRegistry.CHIPPED_NETHERITE_ANVIL)
            entries.add(BlockRegistry.DAMAGED_NETHERITE_ANVIL)
        }
    }

    fun register() {
        Registries.ITEM_GROUP.register(identifierOf("fixed"), FIXED)
    }
}
