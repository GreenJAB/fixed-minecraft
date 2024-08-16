package net.greenjab.fixedminecraft.registry

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry
import net.greenjab.fixedminecraft.registry.block.CopperRailBlock
import net.greenjab.fixedminecraft.registry.block.NetheriteAnvilBlock
import net.greenjab.fixedminecraft.registry.block.OxidizableRailBlock
import net.greenjab.fixedminecraft.util.identifierOf
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockSetType
import net.minecraft.block.Blocks
import net.minecraft.block.DoorBlock
import net.minecraft.block.FenceBlock
import net.minecraft.block.FenceGateBlock
import net.minecraft.block.HangingSignBlock
import net.minecraft.block.MapColor
import net.minecraft.block.Oxidizable
import net.minecraft.block.PillarBlock
import net.minecraft.block.PressurePlateBlock
import net.minecraft.block.SignBlock
import net.minecraft.block.SlabBlock
import net.minecraft.block.StairsBlock
import net.minecraft.block.TrapdoorBlock
import net.minecraft.block.WallHangingSignBlock
import net.minecraft.block.WallSignBlock
import net.minecraft.block.WoodType
import net.minecraft.block.enums.Instrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registries.BLOCK
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier

object BlockRegistry {
    val NETHERITE_ANVIL = block(Blocks.NETHERITE_BLOCK, ::NetheriteAnvilBlock) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    }
    val CHIPPED_NETHERITE_ANVIL = block(Blocks.NETHERITE_BLOCK, ::NetheriteAnvilBlock) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    }
    val DAMAGED_NETHERITE_ANVIL = block(Blocks.NETHERITE_BLOCK, ::NetheriteAnvilBlock) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    }
    @JvmField val COPPER_RAIL = block(Blocks.POWERED_RAIL, { OxidizableRailBlock(Oxidizable.OxidationLevel.UNAFFECTED, it) })
    @JvmField val EXPOSED_COPPER_RAIL = block(Blocks.POWERED_RAIL, { OxidizableRailBlock(Oxidizable.OxidationLevel.EXPOSED, it) })
    @JvmField val WEATHERED_COPPER_RAIL = block(Blocks.POWERED_RAIL, { OxidizableRailBlock(Oxidizable.OxidationLevel.WEATHERED, it) })
    @JvmField val OXIDIZED_COPPER_RAIL = block(Blocks.POWERED_RAIL, { OxidizableRailBlock(Oxidizable.OxidationLevel.OXIDIZED, it) })

    @JvmField val WAXED_COPPER_RAIL = block(Blocks.POWERED_RAIL, ::CopperRailBlock)
    @JvmField val WAXED_EXPOSED_COPPER_RAIL = block(Blocks.POWERED_RAIL, ::CopperRailBlock)
    @JvmField val WAXED_WEATHERED_COPPER_RAIL = block(Blocks.POWERED_RAIL, ::CopperRailBlock)
    @JvmField val WAXED_OXIDIZED_COPPER_RAIL = block(Blocks.POWERED_RAIL, ::CopperRailBlock)


    fun register() {
        BLOCK.register("netherite_anvil", NETHERITE_ANVIL)
        BLOCK.register("chipped_netherite_anvil", CHIPPED_NETHERITE_ANVIL)
        BLOCK.register("damaged_netherite_anvil", DAMAGED_NETHERITE_ANVIL)

        BLOCK.register("copper_rail", COPPER_RAIL)
        BLOCK.register("exposed_copper_rail", EXPOSED_COPPER_RAIL)
        BLOCK.register("weathered_copper_rail", WEATHERED_COPPER_RAIL)
        BLOCK.register("oxidized_copper_rail", OXIDIZED_COPPER_RAIL)
        BLOCK.register("waxed_copper_rail", WAXED_COPPER_RAIL)
        BLOCK.register("waxed_exposed_copper_rail", WAXED_EXPOSED_COPPER_RAIL)
        BLOCK.register("waxed_weathered_copper_rail", WAXED_WEATHERED_COPPER_RAIL)
        BLOCK.register("waxed_oxidized_copper_rail", WAXED_OXIDIZED_COPPER_RAIL)

    }

    private fun registerBlock(identifier: String, block: Block) {
        Registries.BLOCK.register(identifier, block)
    }

    private fun registerBlockWithItem(identifier: String, block: Block, itemSettings: Item.Settings = FabricItemSettings()) {
        registerBlock(identifier, block)
        Registries.ITEM.register(identifier, BlockItem(block, itemSettings))
    }
}
