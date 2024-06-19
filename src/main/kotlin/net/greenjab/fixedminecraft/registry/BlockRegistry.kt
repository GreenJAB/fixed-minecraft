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

    @JvmField val AZALEA_PLANKS: Block = Block(AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
    @JvmField val AZALEA_LOG: Block = Blocks.createLogBlock(MapColor.LIME, MapColor.GREEN)
    @JvmField val STRIPPED_AZALEA_LOG: Block = Blocks.createLogBlock(MapColor.LIME, MapColor.LIME)
    @JvmField val AZALEA_WOOD: Block = PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
    @JvmField val STRIPPED_AZALEA_WOOD: Block = PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable())
    @JvmField val AZALEA_SIGN: Block = SignBlock(
        WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(
            Instrument.BASS).noCollision().strength(1.0F).burnable())
    val AZALEA_WALL_SIGN: Block = WallSignBlock(
        WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(
            Instrument.BASS).noCollision().strength(1.0F).dropsLike(AZALEA_SIGN).burnable())
    val AZALEA_HANGING_SIGN: Block = HangingSignBlock(
        WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(
            Instrument.BASS).noCollision().strength(1.0F).burnable())
    val AZALEA_WALL_HANGING_SIGN: Block = WallHangingSignBlock(
        WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(
            Instrument.BASS).noCollision().strength(1.0F).burnable().dropsLike(AZALEA_HANGING_SIGN))
    val AZALEA_PRESSURE_PLATE: Block = PressurePlateBlock(
        BlockSetType.ACACIA, AbstractBlock.Settings.create().mapColor(AZALEA_PLANKS.getDefaultMapColor()).solid().instrument(
            Instrument.BASS).noCollision().strength(0.5F).burnable().pistonBehavior(PistonBehavior.DESTROY))
    val AZALEA_TRAPDOOR: Block = TrapdoorBlock(
        BlockSetType.ACACIA, AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(
            Instrument.BASS).strength(3.0F).nonOpaque().allowsSpawning(Blocks::never).burnable())
    val AZALEA_BUTTON: Block = Blocks.createWoodenButtonBlock(BlockSetType.ACACIA)
    val AZALEA_STAIRS: Block = StairsBlock(AZALEA_PLANKS.defaultState, AbstractBlock.Settings.copyShallow(AZALEA_PLANKS))
    val AZALEA_SLAB: Block = SlabBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(Instrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable())
    val AZALEA_FENCE_GATE: Block = FenceGateBlock(
        WoodType.ACACIA, AbstractBlock.Settings.create().mapColor(AZALEA_PLANKS.getDefaultMapColor()).solid().instrument(
            Instrument.BASS).strength(2.0F, 3.0F).burnable())
    val AZALEA_FENCE: Block = FenceBlock(AbstractBlock.Settings.create().mapColor(AZALEA_PLANKS.getDefaultMapColor()).instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable().sounds(BlockSoundGroup.WOOD))
    val AZALEA_DOOR: Block = DoorBlock(
        BlockSetType.ACACIA, AbstractBlock.Settings.create().mapColor(AZALEA_PLANKS.getDefaultMapColor()).instrument(
            Instrument.BASS).strength(3.0F).nonOpaque().burnable().pistonBehavior(PistonBehavior.DESTROY))


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


        registerBlockWithItem("azalea_planks", AZALEA_PLANKS)
        registerBlockWithItem("azalea_log", AZALEA_LOG)
        registerBlockWithItem("stripped_azalea_log", STRIPPED_AZALEA_LOG)
        registerBlockWithItem("azalea_wood", AZALEA_WOOD)
        registerBlockWithItem("stripped_azalea_wood", STRIPPED_AZALEA_WOOD)
        registerBlock("azalea_sign", AZALEA_SIGN)
        registerBlock("azalea_wall_sign", AZALEA_WALL_SIGN)
        registerBlock("azalea_hanging_sign", AZALEA_HANGING_SIGN)
        registerBlock("azalea_wall_hanging_sign", AZALEA_WALL_HANGING_SIGN)
        registerBlockWithItem("azalea_pressure_plate", AZALEA_PRESSURE_PLATE)
        registerBlockWithItem("azalea_trapdoor", AZALEA_TRAPDOOR)
        registerBlockWithItem("azalea_button", AZALEA_BUTTON)
        registerBlockWithItem("azalea_stairs", AZALEA_STAIRS)
        registerBlockWithItem("azalea_slab", AZALEA_SLAB)
        registerBlockWithItem("azalea_fence_gate", AZALEA_FENCE_GATE)
        registerBlockWithItem("azalea_fence", AZALEA_FENCE)
        registerBlockWithItem("azalea_door", AZALEA_DOOR)

        StrippableBlockRegistry.register(AZALEA_LOG, STRIPPED_AZALEA_LOG)
        StrippableBlockRegistry.register(AZALEA_WOOD, STRIPPED_AZALEA_WOOD)

        FlammableBlockRegistry.getDefaultInstance().add(AZALEA_PLANKS, 5, 20)
        FlammableBlockRegistry.getDefaultInstance().add(AZALEA_SLAB, 5, 20)
        FlammableBlockRegistry.getDefaultInstance().add(AZALEA_FENCE_GATE, 5, 20)
        FlammableBlockRegistry.getDefaultInstance().add(AZALEA_FENCE, 5, 20)
        FlammableBlockRegistry.getDefaultInstance().add(AZALEA_STAIRS, 5, 20)
        FlammableBlockRegistry.getDefaultInstance().add(AZALEA_LOG, 5, 5)
        FlammableBlockRegistry.getDefaultInstance().add(AZALEA_WOOD, 5, 5)
        FlammableBlockRegistry.getDefaultInstance().add(STRIPPED_AZALEA_LOG, 5, 5)
        FlammableBlockRegistry.getDefaultInstance().add(STRIPPED_AZALEA_WOOD, 5, 5)
    }

    private fun registerBlock(identifier: String, block: Block) {
        Registries.BLOCK.register(identifier, block)
    }

    private fun registerBlockWithItem(identifier: String, block: Block, itemSettings: Item.Settings = FabricItemSettings()) {
        registerBlock(identifier, block)
        Registries.ITEM.register(identifier, BlockItem(block, itemSettings))
    }
}
