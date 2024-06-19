package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.block.BlockState
import net.minecraft.block.IceBlock
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.LightType

class NewIceBlock(settings: Settings) : IceBlock(settings) {
    @Suppress("OVERRIDE_DEPRECATION")
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos) || world.dimension.ultrawarm())
            this.melt(state, world, pos)
    }
}