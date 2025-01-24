package net.greenjab.fixedminecraft.hud

import net.greenjab.fixedminecraft.util.ExhaustionHelper
import net.greenjab.fixedminecraft.util.IntPoint
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper
import java.util.Random
import java.util.Vector
import kotlin.math.ceil


object HUDOverlayHandler {
    private var foodIconsOffset = 0

    private const val FOOD_BAR_HEIGHT = 39

    private var foodBarOffsets: Vector<IntPoint> = Vector<IntPoint>()

    private var random: Random = Random()

    fun onPreRender(context: DrawContext) {
        foodIconsOffset = FOOD_BAR_HEIGHT
        val mc = MinecraftClient.getInstance()
        val player = mc.player
        requireNotNull(player)
        val right = mc.window.scaledWidth / 2 + 91
        val top = mc.window.scaledHeight - foodIconsOffset
        val exhaustion = ExhaustionHelper.getExhaustion(player);
//player.hungerManager.exhaustion
        val renderEvent = HUDOverlayEvent.Exhaustion(exhaustion, right, top, context)

        if (!renderEvent.isCanceled) {
            drawExhaustionOverlay(renderEvent, mc)
        }
    }

    fun onRender(context: DrawContext) {

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

    private fun drawSaturationOverlay(context: DrawContext, saturationLevel: Float, mc: MinecraftClient, right: Int, top: Int) {

        val modifiedSaturation = Math.max(0.0f, Math.min(saturationLevel, 20.0f))
        val endSaturationBar = (ceil(modifiedSaturation / 2)).toInt()
        val iconSize = 9

        for (i in 0 until endSaturationBar) {
            // gets the offset that needs to be render of icon
            val offset = foodBarOffsets.get(i)
            if (offset == null)
                continue

            val x = right + offset.x
            val y = top + offset.y

            val v = 0f
            var u = 0f

            val effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i

            if (effectiveSaturationOfBar > .75)
                u = 3f * iconSize
            else if (effectiveSaturationOfBar > .50)
                u = 2f * iconSize
            else if (effectiveSaturationOfBar > .25)
                u = 1f * iconSize
            context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("fixedminecraft", "textures/icons.png"), x, y, u, v, iconSize, iconSize, 256, 256,  ColorHelper.getWhite(1F))
        }

    }

    private fun drawExhaustionOverlay(context: DrawContext, exhaustion: Float, mc: MinecraftClient, right: Int, top: Int) {
        val maxExhaustion = 1.0f
        // clamp between 0 and 1
        val ratio = (exhaustion / maxExhaustion).coerceIn(0f, 1f)
        val width = (ratio * 81).toInt()
        val height = 9


        context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("fixedminecraft", "textures/icons.png"), right - width, top, 81f - width, 18f, width, height, 128, 16,
            ColorHelper.getWhite(1F))
    }


    private fun drawSaturationOverlay(event: HUDOverlayEvent.Saturation, mc: MinecraftClient) {
        drawSaturationOverlay(event.context, event.saturationLevel, mc, event.x, event.y)
    }


    private fun drawExhaustionOverlay(event: HUDOverlayEvent.Exhaustion, mc: MinecraftClient) {
        drawExhaustionOverlay(event.context, event.exhaustion, mc, event.x, event.y)
    }

    private fun generateBarOffsets(top: Int, left: Int, right: Int, ticks: Int, player: PlayerEntity) {
        val preferFoodBars = 10
        var shouldAnimatedFood = false
        val hungerManager = player.hungerManager

            // in vanilla saturation level is zero will show hunger animation
        val saturationLevel = hungerManager.saturationLevel
        val foodLevel = hungerManager.foodLevel
        shouldAnimatedFood = saturationLevel <= 0.0F && ticks % (foodLevel * 3 + 1) == 0


        // hard code in `InGameHUD`
        random.setSeed((ticks * 312871).toLong())

        if (foodBarOffsets.size != preferFoodBars)
            foodBarOffsets.setSize(preferFoodBars)

        // right alignment, single row
        for (i in 0 until preferFoodBars) {
            val x = right - i * 8 - 9
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
