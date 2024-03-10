package net.greenjab.fixedminecraft.hud

import net.minecraft.client.gui.DrawContext


open class HUDOverlayEvent
private constructor(var x: Int, var y: Int, var context: DrawContext) {

    class Exhaustion(val exhaustion: Float, x: Int, y: Int, context: DrawContext) : HUDOverlayEvent(x, y, context) {
    }

    class Saturation(val saturationLevel: Float, x: Int, y: Int, context: DrawContext) : HUDOverlayEvent(x, y, context) {
    }

    var isCanceled: Boolean = false
}
