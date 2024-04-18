package net.greenjab.fixedminecraft.map_book

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.item.map.MapState

class MapTile(var screen: MapBookScreen, var id: Int, var mapState: MapState, var client: MinecraftClient) : Drawable {

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        if (context == null) return

        val mapScale = (1 shl mapState.scale.toInt()).toFloat()
        val offset = 64*mapScale

        context.matrices.push()

        context.matrices.translate(screen.x, screen.y, (1.0/(mapState.scale.toInt()+1.0))+1.0)
        context.matrices.scale(screen.scale, screen.scale, 1.0f)

        context.matrices.translate(mapState.centerX.toDouble() - offset + context.scaledWindowWidth/2, mapState.centerZ.toDouble() - offset + context.scaledWindowHeight/2,1.0)
        context.matrices.scale(mapScale, mapScale, 1.0f)

        client.gameRenderer.mapRenderer.draw(context.matrices, context.vertexConsumers, id, mapState, false, LightmapTextureManager.MAX_LIGHT_COORDINATE)

        context.matrices.pop()
    }
}