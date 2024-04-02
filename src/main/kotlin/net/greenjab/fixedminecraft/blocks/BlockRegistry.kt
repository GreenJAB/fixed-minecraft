package net.greenjab.fixedminecraft.blocks

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.greenjab.fixedminecraft.blocks.anvil.NetheriteAnvilBlock
import net.greenjab.fixedminecraft.util.blockSettings
import net.greenjab.fixedminecraft.util.identifierOf
import net.greenjab.fixedminecraft.util.register
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockSetType
import net.minecraft.block.Blocks
import net.minecraft.block.DoorBlock
import net.minecraft.block.FenceBlock
import net.minecraft.block.FenceGateBlock
import net.minecraft.block.HangingSignBlock
import net.minecraft.block.MapColor
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

    private fun createOldStairsBlock(block: Block): Block {
        return StairsBlock(block.defaultState, AbstractBlock.Settings.copyShallow(block))
    }

    val AZALEA_PLANKS: Block = Block(AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
    val AZALEA_LOG: Block = Blocks.createLogBlock(MapColor.LIME, MapColor.GREEN)
    val STRIPPED_AZALEA_LOG: Block = Blocks.createLogBlock(MapColor.LIME, MapColor.LIME)
    val AZALEA_WOOD: Block = PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
    val STRIPPED_AZALEA_WOOD: Block = PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
    val AZALEA_SIGN: Block = SignBlock(WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable())
    val AZALEA_WALL_SIGN: Block = WallSignBlock(WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).dropsLike(AZALEA_SIGN).burnable())
    val AZALEA_HANGING_SIGN: Block = HangingSignBlock(WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable())
    val AZALEA_WALL_HANGING_SIGN: Block = WallHangingSignBlock(WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable().dropsLike(AZALEA_HANGING_SIGN))
    val AZALEA_PRESSURE_PLATE: Block = PressurePlateBlock(BlockSetType.ACACIA, AbstractBlock.Settings.create().mapColor(AZALEA_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(0.5F).burnable().pistonBehavior(PistonBehavior.DESTROY))
    val AZALEA_TRAPDOOR: Block = TrapdoorBlock(BlockSetType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(Instrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable())
    val AZALEA_BUTTON: Block = Blocks.createWoodenButtonBlock(BlockSetType.ACACIA)
    val AZALEA_STAIRS: Block = createOldStairsBlock(AZALEA_PLANKS)
    val AZALEA_SLAB: Block = SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
    val AZALEA_FENCE_GATE: Block = FenceGateBlock(WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(AZALEA_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable())
    val AZALEA_FENCE: Block = FenceBlock(AbstractBlock.Settings.create().mapColor(AZALEA_PLANKS.getDefaultMapColor()).instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable().sounds(BlockSoundGroup.WOOD))
    val AZALEA_DOOR: Block = DoorBlock(BlockSetType.ACACIA, AbstractBlock.Settings.create().mapColor(AZALEA_PLANKS.getDefaultMapColor()).instrument(Instrument.BASS).strength(3.0F).nonOpaque().burnable().pistonBehavior(PistonBehavior.DESTROY))



    fun register() {
        registerBlockWithItem(identifierOf("netherite_anvil"), NETHERITE_ANVIL)
        registerBlockWithItem(identifierOf("chipped_netherite_anvil"), CHIPPED_NETHERITE_ANVIL)
        registerBlockWithItem(identifierOf("damaged_netherite_anvil"), DAMAGED_NETHERITE_ANVIL)

        registerBlockWithItem(identifierOf("azalea_planks"), AZALEA_PLANKS)
        registerBlockWithItem(identifierOf("azalea_log"), AZALEA_LOG)
        registerBlockWithItem(identifierOf("stripped_azalea_log"), STRIPPED_AZALEA_LOG)
        registerBlockWithItem(identifierOf("azalea_wood"), AZALEA_WOOD)
        registerBlockWithItem(identifierOf("stripped_azalea_wood"), STRIPPED_AZALEA_WOOD)
        registerBlock(identifierOf("azalea_sign"), AZALEA_SIGN)
        registerBlock(identifierOf("azalea_wall_sign"), AZALEA_WALL_SIGN)
        registerBlock(identifierOf("azalea_hanging_sign"), AZALEA_HANGING_SIGN)
        registerBlock(identifierOf("azalea_wall_hanging_sign"), AZALEA_WALL_HANGING_SIGN)
        registerBlockWithItem(identifierOf("azalea_pressure_plate"), AZALEA_PRESSURE_PLATE)
        registerBlockWithItem(identifierOf("azalea_trapdoor"), AZALEA_TRAPDOOR)
        registerBlockWithItem(identifierOf("azalea_button"), AZALEA_BUTTON)
        registerBlockWithItem(identifierOf("azalea_stairs"), AZALEA_STAIRS)
        registerBlockWithItem(identifierOf("azalea_slab"), AZALEA_SLAB)
        registerBlockWithItem(identifierOf("azalea_fence_gate"), AZALEA_FENCE_GATE)
        registerBlockWithItem(identifierOf("azalea_fence"), AZALEA_FENCE)
        registerBlockWithItem(identifierOf("azalea_door"), AZALEA_DOOR)
    }

    private fun registerBlock(identifier: Identifier, block: Block) {
        Registries.BLOCK.register(identifier, block)
    }

    private fun registerBlockWithItem(identifier: Identifier, block: Block, itemSettings: Item.Settings = FabricItemSettings()) {
        registerBlock(identifier, block)
        Registries.ITEM.register(identifier, BlockItem(block, itemSettings))
    }
}
