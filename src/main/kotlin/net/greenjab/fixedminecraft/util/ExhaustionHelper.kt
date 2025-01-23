package net.greenjab.fixedminecraft.util

import net.minecraft.entity.player.PlayerEntity

object ExhaustionHelper {
    fun getExhaustion(player: PlayerEntity): Float {
        return (player.hungerManager as ExhaustionManipulator).exhaustion
    }

    fun setExhaustion(player: PlayerEntity, exhaustion: Float) {
        (player.hungerManager as ExhaustionManipulator).exhaustion =
            exhaustion
    }

    interface ExhaustionManipulator {
        var exhaustion: Float
    }
}
