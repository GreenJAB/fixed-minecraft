package net.greenjab.fixedminecraft.map_book

import net.greenjab.fixedminecraft.mixin.client.map.DrawContextAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.render.MapRenderState
import net.minecraft.component.type.MapIdComponent
import net.minecraft.item.map.MapState


class MapTile(private val screen: MapBookScreen, id: MapIdComponent?, private val mapState: MapState, private val client: MinecraftClient) :
    Drawable {
    private val mapRenderState = MapRenderState()

    init {
        client.mapRenderer.update(id, mapState, mapRenderState)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val mapScale = (1 shl mapState.scale.toInt()).toFloat()
        val offset = 64f * mapScale
        context.matrices.push()
        context.matrices.translate(screen.x, screen.y, 1.0 / (mapState.scale.toDouble() + 1.0) + 1.0)
        context.matrices.scale(screen.scale, screen.scale, -1.0f)
        context.matrices.translate(
            mapState.centerX.toDouble() - offset.toDouble() + screen.width.toDouble() / 2.0,
            mapState.centerZ.toDouble() - offset.toDouble() + screen.height.toDouble() / 2.0, 0.0
        )
        context.matrices.scale(mapScale, mapScale, 1.0f)
        client.mapRenderer.draw(mapRenderState, context.matrices, (context as DrawContextAccessor).vertexConsumers, true, 15728880)
        context.matrices.pop()
    }
}
