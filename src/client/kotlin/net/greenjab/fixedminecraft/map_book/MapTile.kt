package net.greenjab.fixedminecraft.map_book

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.item.FilledMapItem

class MapTile(var screen: MapBookScreen, var id: Int, var client: MinecraftClient) : Drawable {
    val mapState = FilledMapItem.getMapState(id, client.world)

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        if (context == null || mapState == null) return

        val mapScale = (1 shl mapState.scale.toInt()).toFloat()
        val offset = 64*mapScale

        context.matrices.push()

        context.matrices.translate(screen.x, screen.y,1.0)
        context.matrices.scale(screen.scale, screen.scale, 1.0f)

        context.matrices.translate(mapState.centerX.toDouble() - offset + context.scaledWindowWidth/2, mapState.centerZ.toDouble() - offset + context.scaledWindowHeight/2,1.0)
        context.matrices.scale(mapScale, mapScale, 1.0f)

        client.gameRenderer.mapRenderer.draw(context.matrices, context.vertexConsumers, id, mapState, true, LightmapTextureManager.MAX_LIGHT_COORDINATE)

        context.matrices.pop()
    }
}