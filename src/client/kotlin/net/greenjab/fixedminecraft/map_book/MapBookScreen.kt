package net.greenjab.fixedminecraft.map_book

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.util.math.MathHelper
import kotlin.math.abs

class MapBookScreen(item: ItemStack) : Screen(item.name) {
    var x = 0.0
    var y = 0.0
    var scale = 1.0f
    var targetScale = 1.0f

    override fun init() {
        for (i in 1..16) {
            addDrawable(MapTile(this, i, client!!))
        }

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) { button: ButtonWidget? -> this.close() }.dimensions(width / 2 - 100, height / 4 + 144, 200, 20).build())
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        x += deltaX
        y += deltaY
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (verticalAmount != 0.0) {
            targetScale = zoom(scale, -verticalAmount.toFloat(), 5.0f)
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        if (scale != targetScale) {
            val newScale = MathHelper.lerp(delta, scale, targetScale)
            setScale(newScale, mouseX.toDouble(), mouseY.toDouble())
        }

        super.render(context, mouseX, mouseY, delta)
    }

    fun setScale(newScale: Float, mouseX: Double, mouseY: Double) {
        val offsetX = x-mouseX
        val offsetY = y-mouseY

        val scaleChange = newScale/scale

        x = (scaleChange * offsetX)+mouseX
        y = (scaleChange * offsetY)+mouseY

        scale = newScale
    }

    fun zoom(start: Float, scroll: Float, speed: Float): Float {
        val absScroll = abs(scroll)
        return if (scroll > 0) start - (start / (scroll * speed)) else (start * absScroll * speed) / (absScroll * speed - 1)
    }
}