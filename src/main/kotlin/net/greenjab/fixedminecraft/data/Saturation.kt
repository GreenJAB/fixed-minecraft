package net.greenjab.fixedminecraft.data

import net.minecraft.entity.player.HungerManager
import net.minecraft.entity.player.PlayerEntity
import kotlin.math.min


object Saturation {
    private var SaturationSinceLastHunger = 0.0f

     private var lastExhaustion = 0.0f
     private var ticksSinceLastExhaustion = 0

    fun HungerToSaturation(player: PlayerEntity, hunger: HungerManager) {
        var h = if(player.isSneaking()) 2.0f else 1.0f
        if (hunger.exhaustion == lastExhaustion) {ticksSinceLastExhaustion = min(ticksSinceLastExhaustion+h.toInt(), 40);}
        else {
            ticksSinceLastExhaustion = 0
            lastExhaustion = hunger.exhaustion
        }
        if (hunger.saturationLevel< hunger.foodLevel) {
            if (ticksSinceLastExhaustion == 40) {
                h *=0.015f+hunger.saturationLevel/200.0f
                hunger.saturationLevel = min(hunger.saturationLevel+h,hunger.foodLevel.toFloat());
                SaturationSinceLastHunger += h;
                if (SaturationSinceLastHunger >= 5) {
                    SaturationSinceLastHunger = 0.0f;
                     hunger.foodLevel--;
                }
            }
        } else {
            hunger.saturationLevel= hunger.foodLevel.toFloat();
        }
    }
}
