package net.greenjab.fixedminecraft.blocks.impl

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.AbstractRailBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Oxidizable.OxidationLevel
import net.minecraft.block.enums.RailShape
import net.minecraft.block.enums.RailShape.NORTH_SOUTH
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random

@Suppress("OVERRIDE_DEPRECATION")
open class CopperRailBlock(settings: Settings) : AbstractRailBlock(true, settings) {
    init {
        defaultState = stateManager.defaultState.with(SHAPE, NORTH_SOUTH).with(POWERED, false).with(WATERLOGGED, false)
    }

    override fun getShapeProperty() = SHAPE
    override fun getCodec(): MapCodec<out CopperRailBlock> = CODEC!!

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState = Blocks.POWERED_RAIL.rotate(state, rotation)
    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState = Blocks.POWERED_RAIL.mirror(state, mirror)

    class Oxidizable(private val oxidation: OxidationLevel, settings: Settings) : CopperRailBlock(settings),
                                                                                  net.minecraft.block.Oxidizable {
        override fun getCodec(): MapCodec<Oxidizable> = CODEC
        override fun hasRandomTicks(state: BlockState) = net.minecraft.block.Oxidizable.getIncreasedOxidationBlock(state.block).isPresent
        override fun getDegradationLevel() = oxidation
        override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) =
            tickDegradation(state, world, pos, random)

        companion object {
            val CODEC = RecordCodecBuilder.mapCodec {
                it.group(
                    OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(Oxidizable::getDegradationLevel),
                    createSettingsCodec()
                ).apply(it, CopperRailBlock::Oxidizable)
            }
        }
    }

    companion object {
        val SHAPE: EnumProperty<RailShape> = Properties.STRAIGHT_RAIL_SHAPE
        val POWERED: BooleanProperty = Properties.POWERED
        val CODEC = createCodec(::CopperRailBlock)
    }
}
