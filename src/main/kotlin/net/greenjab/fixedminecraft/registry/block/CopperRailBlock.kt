package net.greenjab.fixedminecraft.registry.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.AbstractRailBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Oxidizable
import net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED
import net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED
import net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED
import net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED
import net.minecraft.block.enums.RailShape
import net.minecraft.block.enums.RailShape.NORTH_SOUTH
import net.minecraft.registry.Registries
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation

@Suppress("OVERRIDE_DEPRECATION")
open class CopperRailBlock(settings: Settings) : AbstractRailBlock(true, settings) {
    internal val level: Oxidizable.OxidationLevel by lazy {
        if (this is OxidizableRailBlock) this.oxidation
        else when (Registries.BLOCK.getId(this).path.removePrefix("waxed_").substringBefore('_')) {
            "exposed" -> EXPOSED
            "weathered" -> WEATHERED
            "oxidized" -> OXIDIZED
            else -> UNAFFECTED
        }
    }

    init {
        this.defaultState = stateManager.defaultState
            .with(SHAPE, NORTH_SOUTH)
            .with(WATERLOGGED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        with(builder) {
            add(SHAPE)
            add(WATERLOGGED)
        }
    }

    override fun getShapeProperty() = SHAPE
    override fun getCodec(): MapCodec<out CopperRailBlock> = CODEC

    //override fun rotate(state: BlockState, rotation: BlockRotation): BlockState = Blocks.POWERED_RAIL.rotate(state, rotation)
    //override fun mirror(state: BlockState, mirror: BlockMirror): BlockState = Blocks.POWERED_RAIL.mirror(state, mirror)

    companion object {
        @JvmField
        val SHAPE: EnumProperty<RailShape> = Properties.STRAIGHT_RAIL_SHAPE


        @JvmField
        val CODEC: MapCodec<CopperRailBlock> = createCodec(::CopperRailBlock)

        @JvmStatic
        fun getMaxVelocity(state: BlockState): Double = when((state.block as CopperRailBlock).level) {
            UNAFFECTED -> 20.0 //15,10,6,3
            EXPOSED -> 15.0
            WEATHERED -> 10.0
            OXIDIZED -> 5.0
        }
    }
}
