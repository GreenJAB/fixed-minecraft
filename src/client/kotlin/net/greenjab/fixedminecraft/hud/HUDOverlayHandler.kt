package net.greenjab.fixedminecraft.hud

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.greenjab.fixedminecraft.helpers.TextureHelper
import net.greenjab.fixedminecraft.hud.HUDOverlayEvent
import net.greenjab.fixedminecraft.util.IntPoint
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerEntity
import org.lwjgl.opengl.GL11
import java.util.Random
import java.util.Vector
import kotlin.math.ceil


object HUDOverlayHandler {
    var foodIconsOffset = 0
    var needDisableBlend = false

    val FOOD_BAR_HEIGHT = 39

    var foodBarOffsets: Vector<IntPoint> = Vector<IntPoint>()

    var random: Random = Random()

    fun onPreRender(context: DrawContext) {
        foodIconsOffset = FOOD_BAR_HEIGHT
        var mc = MinecraftClient.getInstance()
        var player = mc.player
        requireNotNull(player)
        val right = mc.window.scaledWidth / 2 + 91
        val top = mc.window.scaledHeight - foodIconsOffset
        val exhaustion = player.hungerManager.exhaustion
        val renderEvent = HUDOverlayEvent.Exhaustion(exhaustion, right, top, context)

        if (!renderEvent.isCanceled) {
            drawExhaustionOverlay(renderEvent, mc)
        }
    }

    fun onRender(context: DrawContext) {
        //if (!shouldRenderAnyOverlays())
        //    return

        val mc: MinecraftClient = MinecraftClient.getInstance()
        val player: PlayerEntity = mc.player!!
        val stats = player.hungerManager

        val top = mc.window.scaledHeight - foodIconsOffset
        val left = mc.window.scaledWidth / 2 - 91
        val right = mc.window.scaledWidth / 2 + 91

        generateBarOffsets(top, left, right, mc.inGameHud.ticks, player)

        val saturationRenderEvent = HUDOverlayEvent.Saturation(stats.saturationLevel, right, top, context)

        if (!saturationRenderEvent.isCanceled) {
            drawSaturationOverlay(saturationRenderEvent, mc)
        }

    }

    fun drawSaturationOverlay(context: DrawContext, saturationLevel: Float, mc: MinecraftClient, right: Int, top: Int) {

        var modifiedSaturation = Math.max(0.0f, Math.min(saturationLevel, 20.0f))
        var endSaturationBar = (ceil(modifiedSaturation / 2)).toInt()
        var iconSize = 9

        for (i in 0 until endSaturationBar) {
            // gets the offset that needs to be render of icon
            val offset = foodBarOffsets.get(i)
            if (offset == null)
                continue

            val x = right + offset.x
            val y = top + offset.y

            var v = 0
            var u = 0

            val effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i

            if (effectiveSaturationOfBar >= 1)
                u = 3 * iconSize
            else if (effectiveSaturationOfBar > .5)
                u = 2 * iconSize
            else if (effectiveSaturationOfBar > .25)
                u = 1 * iconSize

            context.drawTexture(TextureHelper.MOD_ICONS, x, y, u, v, iconSize, iconSize)
        }

    }

    fun drawExhaustionOverlay(context: DrawContext, exhaustion: Float, mc: MinecraftClient, right: Int, top: Int) {
        val maxExhaustion = 4.0f/*FoodHelper.MAX_EXHAUSTION*/
        // clamp between 0 and 1
        val ratio = (exhaustion / maxExhaustion).coerceIn(0f, 1f)
        val width = (ratio * 81).toInt()
        val height = 9.toInt()

        context.drawTexture(TextureHelper.MOD_ICONS, right - width, top, 81 - width, 18, width, height)
    }


    fun drawSaturationOverlay(event: HUDOverlayEvent.Saturation, mc: MinecraftClient) {
        drawSaturationOverlay(event.context, event.saturationLevel, mc, event.x, event.y)
    }


    fun drawExhaustionOverlay(event: HUDOverlayEvent.Exhaustion, mc: MinecraftClient) {
        drawExhaustionOverlay(event.context, event.exhaustion, mc, event.x, event.y)
    }

    fun generateBarOffsets(top: Int, left: Int, right: Int, ticks: Int, player: PlayerEntity) {
        var preferFoodBars = 10

        var shouldAnimatedFood = false

        // when some mods using custom render, we need to least provide an option to cancel animation
        if (true/*ModConfig.INSTANCE.showVanillaAnimationsOverlay*/) {
            var hungerManager = player.hungerManager

            // in vanilla saturation level is zero will show hunger animation
            var saturationLevel = hungerManager.saturationLevel
            var foodLevel = hungerManager.foodLevel
            shouldAnimatedFood = saturationLevel <= 0.0F && ticks % (foodLevel * 3 + 1) == 0

        }

        // hard code in `InGameHUD`
        random.setSeed((ticks * 312871).toLong())

        if (foodBarOffsets.size != preferFoodBars)
            foodBarOffsets.setSize(preferFoodBars)

        // right alignment, single row
        for (i in 0 until preferFoodBars) {
            var x = right - i * 8 - 9
            var y = top

            // apply the animated offset
            if (shouldAnimatedFood)
                y += random.nextInt(3) - 1

            // reuse the point object to reduce memory usage
            var point = foodBarOffsets.get(i)
            if (point == null) {
                point = IntPoint()
                foodBarOffsets.set(i, point)
            }

            point.x = x - right
            point.y = y - top
        }
    }
}
