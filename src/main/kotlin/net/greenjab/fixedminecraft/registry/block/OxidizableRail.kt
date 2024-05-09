package net.greenjab.fixedminecraft.registry.block

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.Oxidizable
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random

@Suppress("OVERRIDE_DEPRECATION")
class OxidizableRail(private val oxidation: Oxidizable.OxidationLevel, settings: Settings) : CopperRailBlock(settings),
                                                                                             Oxidizable {
    override fun getCodec(): MapCodec<OxidizableRail> = CODEC
    override fun hasRandomTicks(state: BlockState) = Oxidizable.getIncreasedOxidationBlock(state.block).isPresent
    override fun getDegradationLevel() = oxidation
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) =
        tickDegradation(state, world, pos, random)

    companion object {
        val CODEC: MapCodec<OxidizableRail> = RecordCodecBuilder.mapCodec {
            it.group(
                Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state")
                    .forGetter(OxidizableRail::getDegradationLevel),
                createSettingsCodec()
            ).apply(it, ::OxidizableRail)
        }
    }
}
