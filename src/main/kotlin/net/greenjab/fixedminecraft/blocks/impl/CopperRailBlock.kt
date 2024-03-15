package net.greenjab.fixedminecraft.blocks.impl

import com.mojang.serialization.MapCodec
import net.minecraft.block.AbstractRailBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.enums.RailShape
import net.minecraft.block.enums.RailShape.NORTH_SOUTH
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation

@Suppress("OVERRIDE_DEPRECATION")
open class CopperRailBlock(settings: Settings) : AbstractRailBlock(true, settings) {
    init {
        defaultState = stateManager.defaultState.with(SHAPE, NORTH_SOUTH).with(POWERED, false).with(WATERLOGGED, false)
    }

    override fun getShapeProperty() = SHAPE
    override fun getCodec(): MapCodec<out CopperRailBlock> = CODEC

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState = Blocks.POWERED_RAIL.rotate(state, rotation)
    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState = Blocks.POWERED_RAIL.mirror(state, mirror)

    companion object {
        val SHAPE: EnumProperty<RailShape> = Properties.STRAIGHT_RAIL_SHAPE
        val POWERED: BooleanProperty = Properties.POWERED
        val CODEC: MapCodec<CopperRailBlock> = createCodec(::CopperRailBlock)
    }
}
