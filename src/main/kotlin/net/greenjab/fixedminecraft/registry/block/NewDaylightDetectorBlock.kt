package net.greenjab.fixedminecraft.registry.block

import net.minecraft.block.BlockState
import net.minecraft.block.DaylightDetectorBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class NewDaylightDetectorBlock(settings: Settings) : DaylightDetectorBlock(settings) {
    override fun hasComparatorOutput(state: BlockState): Boolean {
        return true
    }
    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
        if (!world.isClient && world.dimension.hasSkyLight()) {
            val bl = state.get(INVERTED) as Boolean
            return if (bl) {
                world.moonPhase+1
            } else {
                (((world.timeOfDay+5000)%12000)/1000).toInt()+1
            }
        }
        return 0
    }
}
