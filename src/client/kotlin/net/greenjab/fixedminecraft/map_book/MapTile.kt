package net.greenjab.fixedminecraft.map_book

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.MapColor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.map.MapState
import net.minecraft.util.Identifier

class MapTile(var screen: MapBookScreen, var id: Int, var mapState: MapState, var client: MinecraftClient) : Drawable {

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val mapScale = (1 shl mapState.scale.toInt()).toFloat()
        val offset = 64*mapScale

        context.matrices.push()

        // the z scale and translating ensures larger maps render behind smaller ones, but it messes up when markers are on the border of a map
        // it should be possible to have the best of both worlds by increasing the -1.0 scale, but its not worth doing the maths yet
        context.matrices.translate(screen.x, screen.y, (1.0/(mapState.scale.toInt()+1.0))+1.0)
        context.matrices.scale(screen.scale, screen.scale, -1.0f)

        context.matrices.translate(mapState.centerX.toDouble() - offset + screen.width/2.0, mapState.centerZ.toDouble() - offset + screen.height/2.0,0.0)
        context.matrices.scale(mapScale, mapScale, 1.0f)

         client.gameRenderer.mapRenderer.draw(context.matrices, context.vertexConsumers, id, mapState, true, 15728879)

        context.matrices.pop()
    }
}
