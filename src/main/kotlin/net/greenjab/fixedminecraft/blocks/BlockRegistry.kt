package net.greenjab.fixedminecraft.blocks

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.greenjab.fixedminecraft.blocks.anvil.NetheriteAnvilBlock
import net.greenjab.fixedminecraft.util.blockSettings
import net.greenjab.fixedminecraft.util.identifierOf
import net.greenjab.fixedminecraft.util.register
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier

object BlockRegistry {
    val NETHERITE_ANVIL: Block = NetheriteAnvilBlock(blockSettings(Blocks.NETHERITE_BLOCK) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    })

    val CHIPPED_NETHERITE_ANVIL: Block = NetheriteAnvilBlock(blockSettings(Blocks.NETHERITE_BLOCK) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    })

    val DAMAGED_NETHERITE_ANVIL: Block = NetheriteAnvilBlock(blockSettings(Blocks.NETHERITE_BLOCK) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    })


    fun register() {
        registerBlockWithItem(identifierOf("netherite_anvil"), NETHERITE_ANVIL)
        registerBlockWithItem(identifierOf("chipped_netherite_anvil"), CHIPPED_NETHERITE_ANVIL)
        registerBlockWithItem(identifierOf("damaged_netherite_anvil"), DAMAGED_NETHERITE_ANVIL)
    }

    private fun registerBlock(identifier: Identifier, block: Block) {
        Registries.BLOCK.register(identifier, block)
    }

    private fun registerBlockWithItem(identifier: Identifier, block: Block, itemSettings: Item.Settings = FabricItemSettings()) {
        registerBlock(identifier, block)
        Registries.ITEM.register(identifier, BlockItem(block, itemSettings))
    }
}
