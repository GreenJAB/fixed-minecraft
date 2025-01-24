package net.greenjab.fixedminecraft.map_book

import net.greenjab.fixedminecraft.mixin.client.map.DrawContextAccessor
import net.greenjab.fixedminecraft.registry.ItemRegistry
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookPlayer
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.RenderLayer
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.MapIdComponent
import net.minecraft.item.ItemStack
import net.minecraft.item.map.MapDecoration
import net.minecraft.item.map.MapDecorationTypes
import net.minecraft.screen.ScreenTexts
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.Objects
import java.util.Optional
import kotlin.math.abs


class MapBookScreen(var item: ItemStack) : Screen(item.name) {
    var x = 0.0
    var y = 0.0
    var scale = 1.0f
    private var targetScale = 0.5f
    private val MAP_ICONS_RENDER_LAYER: RenderLayer = RenderLayer.getText(Identifier.of("textures/map/map_icons.png"))

    override fun init() {
        scale = 1.0f
        targetScale = 0.5f
        if (client != null && client!!.player != null) {
            x = -client!!.player!!.x
            y = -client!!.player!!.z
        }
        setScale(targetScale, width/2.0, height/2.0)
        for (mapStateData in (ItemRegistry.MAP_BOOK as MapBookItem).getMapStates(item, client?.world as World)) {
            addDrawable(MapTile(this, mapStateData.id, mapStateData.mapState, client!!))
        }
        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) { button : ButtonWidget -> this.close() }.dimensions(width / 2 - 100, height -40, 200, 20).build())
    }

    override fun shouldPause(): Boolean {
        return false
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 2) {
            if (client?.player?.abilities?.creativeMode == true) {
                var pos = Vec3d(mouseX, mouseY, 0.0)
                pos = pos.multiply((1/scale).toDouble())
                pos = pos.subtract(width / 2.0, height / 2.0, 0.0)
                pos = pos.subtract(this.x/scale, this.y/scale, 0.0)
                MinecraftClient.getInstance().networkHandler?.sendCommand(String.format(
                    "tp %.6f %.6f %.6f",
                    pos.getX(), client?.player?.y, pos.getY()))
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        x += deltaX
        y += deltaY
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (verticalAmount != 0.0) {
            targetScale = zoom(scale, -verticalAmount.toFloat())
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

        val thisPlayer = client?.player ?: return

        var stack = client?.player?.mainHandStack
        if (stack != null) {
            if (stack.item !is MapBookItem) stack = client?.player?.offHandStack
        }
        if (stack == null) return



        // val tag: NbtCompound? = stack?.nbt
        var id = -1

        if (stack.contains(DataComponentTypes.MAP_ID)) {
            id = stack.get(DataComponentTypes.MAP_ID)?.id ?: -1
        }

        // if (tag != null && tag.contains("fixedminecraft:map_book")) {
        //    id = tag.getInt("fixedminecraft:map_book")
        //}
        if (id != -1) {
            val p = MapBookPlayer()
            p.setPlayer(thisPlayer)
            val m = MapBookStateManager.INSTANCE.getClientMapBookState(id)?.players
            if (m != null) {
                try {
                    for (player in m) {
                        if (player.dimension == p.dimension) {
                            if (player.name != p.name) {
                                renderPlayerIcon(context, player)
                            }
                        }
                    }
                } catch (e: ConcurrentModificationException) {
                }
            }
            renderPlayerIcon(context, p)
            renderIcons(context)
            renderPosition(context, mouseX, mouseY)
        }
    }

    private fun renderPosition(context: DrawContext, mouseX: Int, mouseY: Int) {

        var pos = Vec3d(mouseX.toDouble(), mouseY.toDouble(), 0.0)
        pos = pos.multiply((1/scale).toDouble())
        pos = pos.subtract(width / 2.0, height / 2.0, 0.0)
        pos = pos.subtract(this.x/scale, this.y/scale, 0.0)


        val textRenderer = MinecraftClient.getInstance().textRenderer
        val text = ""+pos.getX().toInt() + ", " + pos.getY().toInt()
        val o = textRenderer.getWidth(text).toFloat()
        Objects.requireNonNull(textRenderer)
        context.matrices.push()

        context.matrices.translate(width / 2.0, height -60.0, 20.0)

        context.matrices.translate(-o / 2f, 8.0f, 0.1f)

        textRenderer.draw(
            text,
            0.0f,
            0.0f,
            -1,
            false,
            context.matrices.peek().getPositionMatrix(),
            (context as DrawContextAccessor).vertexConsumers,
            TextLayerType.NORMAL,
            Int.MIN_VALUE,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )
        context.matrices.pop()

    }

    private fun renderPlayerIcon(context: DrawContext, player: MapBookPlayer) {
        val x = player.x.toFloat()
        val z = player.z.toFloat()
        val rotation = player.yaw

        context.matrices.push()
        context.matrices.translate(this.x, this.y, 0.0)
        context.matrices.scale(this.scale, this.scale, 1.0f)
        context.matrices.translate(x.toDouble() + width.toDouble() / 2.0, z.toDouble() + height.toDouble() / 2.0, 0.0)
        context.matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation))
        context.matrices.scale(8.0f, 8.0f, -3.0f)
        context.matrices.translate(-0.125f, 0.125f, -10.0f)
        context.matrices.scale(1f / this.scale, 1f / this.scale, 1.0f)
        val sprite = client!!.mapDecorationsAtlasManager.getSprite(
            MapDecoration(
                MapDecorationTypes.PLAYER,
                0.toByte(),
                0.toByte(),
                0.toByte(),
                Optional.empty()
            )
        )
        val g = sprite.minU
        val h = sprite.minV
        val l = sprite.maxU
        val m = sprite.maxV
        val matrix4f2 = context.matrices.peek().positionMatrix
        val vertexConsumer2 = (context as DrawContextAccessor).vertexConsumers.getBuffer(RenderLayer.getText(sprite.atlasId))
        vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(g, h).light(15728880)
        vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(l, h).light(15728880)
        vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(l, m).light(15728880)
        vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(g, m).light(15728880)
        context.matrices.pop()



        val textRenderer = MinecraftClient.getInstance().textRenderer
        val text = player.name
        val o = textRenderer.getWidth(text).toFloat()
        Objects.requireNonNull(textRenderer)
        context.matrices.push()

        context.matrices.translate(this.x, this.y, 15.0)
        context.matrices.scale(this.scale, this.scale, 1.0f)

        context.matrices.translate(x + width / 2.0, z + height / 2.0, 0.0)

        context.matrices.scale(1 / this.scale, 1 / this.scale, 1.0f)
        context.matrices.translate(-o / 2f, 8.0f, 0.1f)

        textRenderer.draw(
            text,
            0.0f,
            0.0f,
            -1,
            false,
            context.matrices.peek().getPositionMatrix(),
            context.vertexConsumers,
            TextLayerType.NORMAL,
            Int.MIN_VALUE,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )
        context.matrices.pop()

    }


    private fun renderIcons(context: DrawContext) {
        var k = 0

        val light = LightmapTextureManager.MAX_LIGHT_COORDINATE

        var stack = client?.player?.mainHandStack
        if (stack != null) {
            if (stack.item !is MapBookItem) stack = client?.player?.offHandStack
        }

        for (mapStateData in getMapStates(stack!!, client?.world as World)) {

            val var11: Iterator<*> = mapStateData.mapState.banners.iterator()
            while (var11.hasNext()) {

                val mapIcon: MapDecoration = var11.next() as MapDecoration
                if (mapIcon.type.idAsString !="player" && mapIcon.type.idAsString !="player_off_map" && mapIcon.type.idAsString !="player_off_limits") {

                    context.matrices.push()
                    context.matrices.translate(this.x, this.y, 0.0)
                    context.matrices.scale(this.scale, this.scale, 1.0f)
                    context.matrices.translate(mapIcon.x + width.toDouble() / 2.0, mapIcon.z.toDouble() + height.toDouble() / 2.0, 0.0)
                    context.matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(mapIcon.rotation.toFloat()))
                    context.matrices.scale(8.0f, 8.0f, -3.0f)
                    context.matrices.translate(-0.125f, 0.125f, -10.0f)
                    context.matrices.scale(1f / this.scale, 1f / this.scale, 1.0f)
                    val sprite = client!!.mapDecorationsAtlasManager.getSprite(
                        MapDecoration(
                            mapIcon.type,
                            0.toByte(),
                            0.toByte(),
                            0.toByte(),
                            Optional.empty()
                        )
                    )
                    val g = sprite.minU
                    val h = sprite.minV
                    val l = sprite.maxU
                    val m = sprite.maxV
                    val matrix4f2 = context.matrices.peek().positionMatrix
                    val vertexConsumer2 = (context as DrawContextAccessor).vertexConsumers.getBuffer(RenderLayer.getText(sprite.atlasId))
                    vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(g, h).light(15728880)
                    vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(l, h).light(15728880)
                    vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(l, m).light(15728880)
                    vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(g, m).light(15728880)
                    context.matrices.pop()


                    if (mapIcon.name != null) {
                        val textRenderer = MinecraftClient.getInstance().textRenderer
                        val text = mapIcon.name.get()
                        val o = textRenderer.getWidth(text).toFloat()
                        Objects.requireNonNull(textRenderer)
                        context.matrices.push()

                        context.matrices.translate(this.x, this.y, 11.0)
                        context.matrices.scale(this.scale, this.scale, 1.0f)
                        context.matrices.translate(
                            mapStateData.mapState.centerX.toDouble() + mapIcon.x * this.scale / 2.0 + this.width / 2.0,
                            mapStateData.mapState.centerZ.toDouble() + (mapIcon.z + 1) * this.scale / 2.0 + this.height / 2.0,
                            0.0
                        )
                        context.matrices.scale(1 / this.scale, 1 / this.scale, 1.0f)
                        context.matrices.translate(-o / 2f, 8.0f, 0.1f)

                        textRenderer.draw(
                            text,
                            0.0f,
                            0.0f,
                            -1,
                            false,
                            context.matrices.peek().getPositionMatrix(),
                            context.vertexConsumers,
                            TextLayerType.NORMAL,
                            Int.MIN_VALUE,
                            light
                        )
                        context.matrices.pop()
                    }
                    ++k
                }
            }
        }
    }


    fun getMapStates(stack: ItemStack, world: World): ArrayList<MapStateData> {
        val list = ArrayList<MapStateData>()
        val mapBookState = getMapBookState(stack, world)

        if (mapBookState != null) {
            for (i in mapBookState.mapIDs) {
                val mapState = world.getMapState(MapIdComponent(i))
                if (mapState != null) {
                    list.add(MapStateData(MapIdComponent(i), mapState))
                }
            }
        }
        return list
    }

    private fun getMapBookState(stack: ItemStack, world: World): MapBookState? {
        val id = getMapBookId(stack) ?: return null

        if (world.isClient) {
            return MapBookStateManager.INSTANCE.getClientMapBookState(id)
        } else if (world.server != null) {
            return MapBookStateManager.INSTANCE.getMapBookState(world.server!!, id)
        }
        return null
    }

    fun getMapBookId(stack: ItemStack): Int? {
        val mapIdComponent = stack.getOrDefault(DataComponentTypes.MAP_ID, null)
        return mapIdComponent?.id()
    }

    /*private fun getMapBookId(stack: ItemStack?): Int? {
        val nbtCompound = stack?.nbt
        return if (nbtCompound != null && nbtCompound.contains(
                "fixedminecraft:map_book",
                NbtElement.NUMBER_TYPE.toInt()
            )
        ) Integer.valueOf(nbtCompound.getInt("fixedminecraft:map_book")) else null
    }*/

    private fun setScale(newScale: Float, mouseX: Double, mouseY: Double) {
        val offsetX = x-mouseX
        val offsetY = y-mouseY

        val scaleChange = newScale/scale

        x = (scaleChange * offsetX)+mouseX
        y = (scaleChange * offsetY)+mouseY

        scale = newScale
    }

    private fun zoom(start: Float, scroll: Float): Float {
        // logarithmic zoom that doesn't drift when zooming in and out repeatedly
        val absScroll = abs(scroll)
        val speed = 5.0f
        var newZoom = if (scroll > 0) start - (start / (scroll * speed)) else (start * absScroll * speed) / (absScroll * speed - 1)
        newZoom = Math.min(Math.max(newZoom, 0.01f), 10f)
        return newZoom
    }
}
