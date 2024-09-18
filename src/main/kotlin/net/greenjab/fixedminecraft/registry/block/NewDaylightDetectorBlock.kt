package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.block.BlockState
import net.minecraft.block.DaylightDetectorBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.LightType
import net.minecraft.world.World

class NewDaylightDetectorBlock(settings: Settings) : DaylightDetectorBlock(settings) {
    @Suppress("OVERRIDE_DEPRECATION")
    override fun hasComparatorOutput(state: BlockState): Boolean {
        return true
    }
    @Suppress("OVERRIDE_DEPRECATION")
    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
        if (!world.isClient && world.dimension.hasSkyLight()) {
            val bl = state.get(INVERTED) as Boolean
            if (bl) {
                return world.moonPhase+1
            } else {
                return (((world.timeOfDay+5000)%12000)/1000).toInt()+1
            }
        }
        return 0
    }
}
