package net.greenjab.fixedminecraft.map_book

import net.greenjab.fixedminecraft.registry.ItemRegistry
import net.greenjab.fixedminecraft.items.map_book.MapBookItem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.*
import net.minecraft.item.ItemStack
import net.minecraft.item.map.MapIcon
import net.minecraft.screen.ScreenTexts
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import org.joml.Matrix4f
import kotlin.math.abs

class MapBookScreen(var item: ItemStack) : Screen(item.name) {
    var x = 0.0
    var y = 0.0
    var scale = 1.0f
    private var targetScale = 0.5f

    private val MAP_ICONS_RENDER_LAYER: RenderLayer = RenderLayer.getText(Identifier("textures/map/map_icons.png"))

    override fun init() {
        if (client != null && client!!.player != null) {
            x = -client!!.player!!.x
            y = -client!!.player!!.z
        }
        setScale(targetScale, width/2.0, height/2.0)

        for (mapStateData in (ItemRegistry.MAP_BOOK as MapBookItem).getMapStates(item, client?.world)) {
            addDrawable(MapTile(this, mapStateData.id, mapStateData.mapState, client!!))
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
        if (context == null) return

        if (scale != targetScale) {
            val newScale = MathHelper.lerp(delta, scale, targetScale)
            setScale(newScale, mouseX.toDouble(), mouseY.toDouble())
        }

        super.render(context, mouseX, mouseY, delta)

        val player = client?.player ?: return
        renderPlayerIcon(context, player.x.toFloat(), player.z.toFloat(), player.yaw)
    }

    private fun renderPlayerIcon(context: DrawContext, x: Float, z: Float, rotation: Float) {
        context.matrices.push()

        context.matrices.translate(this.x, this.y, 0.0)
        context.matrices.scale(this.scale, this.scale, 1.0f)

        context.matrices.translate(x + width/2.0, z + height/2.0, 0.0)

        context.matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation))
        context.matrices.scale(8.0f, 8.0f, -3.0f)

        context.matrices.translate(-0.125f, 0.125f, -10f)
        context.matrices.scale(1/this.scale, 1/this.scale, 1.0f)
        val b: Byte = MapIcon.Type.PLAYER.id
        val g = (b % 16 + 0).toFloat() / 16.0f
        val h = (b / 16 + 0).toFloat() / 16.0f
        val l = (b % 16 + 1).toFloat() / 16.0f
        val m = (b / 16 + 1).toFloat() / 16.0f
        val matrix4f2: Matrix4f = context.matrices.peek().positionMatrix
        val vertexConsumer2: VertexConsumer = context.vertexConsumers.getBuffer(MAP_ICONS_RENDER_LAYER)
        vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(g, h)
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next()
        vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(l, h)
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next()
        vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(l, m)
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next()
        vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(g, m)
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next()
        context.matrices.pop()
    }

    private fun setScale(newScale: Float, mouseX: Double, mouseY: Double) {
        val offsetX = x-mouseX
        val offsetY = y-mouseY

        val scaleChange = newScale/scale

        x = (scaleChange * offsetX)+mouseX
        y = (scaleChange * offsetY)+mouseY

        scale = newScale
    }

    private fun zoom(start: Float, scroll: Float, speed: Float): Float {
        // logarithmic zoom that doesn't drift when zooming in and out repeatedly
        val absScroll = abs(scroll)
        return if (scroll > 0) start - (start / (scroll * speed)) else (start * absScroll * speed) / (absScroll * speed - 1)
    }
}
