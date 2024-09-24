package net.greenjab.fixedminecraft.data

import net.minecraft.entity.player.HungerManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3d
import kotlin.math.min


object Saturation {
    private var SaturationSinceLastHunger = 0.0f

     private var lastExhaustion = 0.0f
     private var ticksSinceLastExhaustion = 0

    public var lastPos = Vec3d(0.0, 0.0, 0.0);
    public var lastPos2 = Vec3d(0.0, 0.0, 0.0);

    public var airTime = 0;

    fun hungerToSaturation(player: PlayerEntity, hunger: HungerManager) {
        // player.addCommandTag("airTime");
        // player.tag

        if (player.isOnGround) airTime=0;
        else if (player.getAbilities().flying) airTime = 10;
        else airTime++;

        var h = if(player.isSneaking()) 2.0f else 1.0f
        if (hunger.exhaustion == lastExhaustion) {ticksSinceLastExhaustion = min(ticksSinceLastExhaustion+h.toInt(), 40);}
        else {
            ticksSinceLastExhaustion = 0
            lastExhaustion = hunger.exhaustion
        }
        if (player.hurtTime>0) {
            ticksSinceLastExhaustion = 0;
        }
        if (hunger.saturationLevel< hunger.foodLevel) {
            if (ticksSinceLastExhaustion == 40) {
                h *=0.015f+hunger.saturationLevel/200.0f
                hunger.saturationLevel = min(hunger.saturationLevel+h,hunger.foodLevel.toFloat());
                SaturationSinceLastHunger += h  * (if(player.isSneaking()) 2.0f else 1.0f);
                if (SaturationSinceLastHunger >= 10) {
                    SaturationSinceLastHunger = 0.0f;
                     hunger.foodLevel--;
                }
            }
        }
    }
}
