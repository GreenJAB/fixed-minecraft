package net.greenjab.fixedminecraft.blocks

import net.greenjab.fixedminecraft.blocks.impl.CopperRailBlock
import net.greenjab.fixedminecraft.blocks.impl.NetheriteAnvilBlock
import net.greenjab.fixedminecraft.util.blockSettings
import net.greenjab.fixedminecraft.util.identifierOf
import net.greenjab.fixedminecraft.util.register
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.Oxidizable.OxidationLevel
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.sound.BlockSoundGroup

object BlockRegistry {
    val NETHERITE_ANVIL: Block = NetheriteAnvilBlock(blockSettings(Blocks.NETHERITE_BLOCK) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    }).register("netherite_anvil") {
        fireproof()
    }

    val CHIPPED_NETHERITE_ANVIL: Block = NetheriteAnvilBlock(blockSettings(Blocks.NETHERITE_BLOCK) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    }).register("chipped_netherite_anvil") {
        fireproof()
    }

    val DAMAGED_NETHERITE_ANVIL: Block = NetheriteAnvilBlock(blockSettings(Blocks.NETHERITE_BLOCK) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    }).register("damaged_netherite_anvil") {
        fireproof()
    }

    val COPPER_RAIL =
        CopperRailBlock.Oxidizable(OxidationLevel.UNAFFECTED, Settings.copy(Blocks.POWERED_RAIL)).register("copper_rail")
    val EXPOSED_COPPER_RAIL =
        CopperRailBlock.Oxidizable(OxidationLevel.EXPOSED, Settings.copy(Blocks.POWERED_RAIL)).register("exposed_copper_rail")
    val WEATHERED_COPPER_RAIL =
        CopperRailBlock.Oxidizable(OxidationLevel.WEATHERED, Settings.copy(Blocks.POWERED_RAIL)).register("weathered_copper_rail")
    val OXIDIZED_COPPER_RAIL =
        CopperRailBlock.Oxidizable(OxidationLevel.OXIDIZED, Settings.copy(Blocks.POWERED_RAIL)).register("oxidized_copper_rail")

    val WAXED_COPPER_RAIL =
        CopperRailBlock(Settings.copy(Blocks.POWERED_RAIL)).register("waxed_copper_rail")
    val WAXED_EXPOSED_COPPER_RAIL =
        CopperRailBlock(Settings.copy(Blocks.POWERED_RAIL)).register("waxed_exposed_copper_rail")
    val WAXED_WEATHERED_COPPER_RAIL =
        CopperRailBlock(Settings.copy(Blocks.POWERED_RAIL)).register("waxed_weathered_copper_rail")
    val WAXED_OXIDIZED_COPPER_RAIL =
        CopperRailBlock(Settings.copy(Blocks.POWERED_RAIL)).register("waxed_oxidized_copper_rail")

    // Prevents referencing in mixins from causing a nasty crash
    private val deferredRegistry = mutableListOf<Triple<String, Block, (Item.Settings.() -> Unit)?>>()
    private fun <T : Block> T.register(id: String, settings: (Item.Settings.() -> Unit)? = null): T {
        deferredRegistry += Triple(id, this, settings)
        return this
    }

    fun register() {
        deferredRegistry.forEach { (id, block, func) ->
            val identifier = identifierOf(id)
            val settings = Item.Settings()
            if (func != null) settings.apply(func)
            Registries.BLOCK.register(identifier, block)
            Registries.ITEM.register(identifier, BlockItem(block, settings))
        }
    }
}
