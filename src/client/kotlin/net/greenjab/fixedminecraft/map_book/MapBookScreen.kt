package net.greenjab.fixedminecraft.map_book

import net.greenjab.fixedminecraft.FixedMinecraft.SERVER
import net.greenjab.fixedminecraft.registry.ItemRegistry
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.map.MapIcon
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.screen.ScreenTexts
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Matrix4f
import java.util.Objects
import kotlin.math.abs

class MapBookScreen(var item: ItemStack) : Screen(item.name) {
    var x = 0.0
    var y = 0.0
    var scale = 1.0f
    private var targetScale = 0.5f

    private val MAP_ICONS_RENDER_LAYER: RenderLayer = RenderLayer.getText(Identifier("textures/map/map_icons.png"))

    override fun init() {
        scale = 1.0f
        targetScale = 0.5f
        if (client != null && client!!.player != null) {
            x = -client!!.player!!.x
            y = -client!!.player!!.z
        }
        setScale(targetScale, width/2.0, height/2.0)

        for (mapStateData in (ItemRegistry.MAP_BOOK as MapBookItem).getMapStates(item, client?.world)) {
            addDrawable(MapTile(this, mapStateData.id, mapStateData.mapState, client!!))
        }

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) { button: ButtonWidget? -> this.close() }.dimensions(width / 2 - 100, height -40, 200, 20).build())
    }

    override fun shouldPause(): Boolean {
        return false
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

        val thisPlayer = client?.player ?: return

        var stack = client?.player?.mainHandStack
        if (stack != null) {
            if (stack.item !is MapBookItem) stack = client?.player?.offHandStack
        }
        val tag: NbtCompound? = stack?.nbt
        var id = -1;
        if (tag != null && tag.contains("fixedminecraft:map_book")) {
            id = tag.getInt("fixedminecraft:map_book")
        }
        for (player in SERVER!!.playerManager.playerList) {
            if (thisPlayer.world.dimensionKey == player.world.dimensionKey) {
                if (hasMapBook(player, id)) {
                    renderPlayerIcon(context, player)
                }
            }
        }
        renderIcons(context);
        renderPosition(context, mouseX, mouseY);
    }
    fun hasMapBook(player: PlayerEntity, id: Int): Boolean {
        if (player.offHandStack.isOf(ItemRegistry.MAP_BOOK)) {
            val tag: NbtCompound? = player.offHandStack?.nbt
            if (tag != null && tag.contains("fixedminecraft:map_book")) {
               if (id == tag.getInt("fixedminecraft:map_book")) return true
            }
        }
        for (item in player.inventory.main) {
            if (item.isOf(ItemRegistry.MAP_BOOK)) {
                val tag: NbtCompound? = item?.nbt
                if (tag != null && tag.contains("fixedminecraft:map_book")) {
                    if (id == tag.getInt("fixedminecraft:map_book")) return true
                }
            }
        }
        return false
    }

    private fun renderPosition(context: DrawContext, mouseX: Int, mouseY: Int) {

        var pos = Vec3d(mouseX.toDouble(), mouseY.toDouble(), 0.0);
        pos = pos.multiply((1/scale).toDouble())
        pos = pos.subtract(width / 2.0, height / 2.0, 0.0)
        pos = pos.subtract(this.x/scale, this.y/scale, 0.0)


        val textRenderer = MinecraftClient.getInstance().textRenderer
        val text = ""+pos.getX().toInt() + ", " + pos.getY().toInt()
        val o = textRenderer.getWidth(text).toFloat()
        val var10000 = 25.0f / o
        Objects.requireNonNull(textRenderer)
        val p = MathHelper.clamp(var10000, 0.0f, 6.0f / 9.0f)
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
            context.vertexConsumers,
            TextLayerType.NORMAL,
            Int.MIN_VALUE,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )
        context.matrices.pop()

    }

    private fun renderPlayerIcon(context: DrawContext, player: PlayerEntity) {
        var x = player.x.toFloat()
        var z = player.z.toFloat()
        var rotation = player.yaw
        context.matrices.push()

        context.matrices.translate(this.x, this.y, 0.0)
        context.matrices.scale(this.scale, this.scale, 1.0f)

        context.matrices.translate(x + width / 2.0, z + height / 2.0, 0.0)

        context.matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation))
        context.matrices.scale(8.0f, 8.0f, 1.0f)

        context.matrices.translate(0f, 0f, 15f)
        context.matrices.scale(1 / this.scale, 1 / this.scale, 1.0f)
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


        val textRenderer = MinecraftClient.getInstance().textRenderer
        val text = player.name.literalString
        val o = textRenderer.getWidth(text).toFloat()
        val var10000 = 25.0f / o
        Objects.requireNonNull(textRenderer)
        val p = MathHelper.clamp(var10000, 0.0f, 6.0f / 9.0f)
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

        var light = LightmapTextureManager.MAX_LIGHT_COORDINATE

        var stack = client?.player?.mainHandStack
        if (stack != null) {
            if (stack.item !is MapBookItem) stack = client?.player?.offHandStack
        }

        for (mapStateData in getMapStates(stack, client?.world)) {

            val var11: Iterator<*> = mapStateData.mapState.getIcons().iterator()
            while (var11.hasNext()) {

                var mapIcon: MapIcon = var11.next() as MapIcon
                if (mapIcon.typeId.toInt() !=0 && mapIcon.typeId.toInt() !=6 && mapIcon.typeId.toInt() !=7) {

                    context.matrices.push()

                    context.matrices.translate(this.x, this.y, 10.0)
                    context.matrices.scale(this.scale, this.scale, 1.0f)


                    val mapScale = (1 shl mapStateData.mapState.scale.toInt()).toFloat()
                    context.matrices.translate(
                        mapStateData.mapState.centerX.toDouble() /*- offset*/ + mapIcon.x * mapScale / 2.0 + this.width / 2.0,
                        mapStateData.mapState.centerZ.toDouble() /*- offset*/ + (mapIcon.z+1) * mapScale / 2.0 + this.height / 2.0,
                        0.0
                    )
                    context.matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((mapIcon.rotation() * 360).toFloat() / 16.0f))
                    context.matrices.scale(8.0f, 8.0f, 1.0f)

                    context.matrices.scale(1 / this.scale, 1 / this.scale, 1.0f)
                    context.matrices.translate(-0.125f, 0.125f, 0.0f)

                    var b = mapIcon.typeId
                    val g = (b % 16 + 0).toFloat() / 16.0f
                    val h = (b / 16 + 0).toFloat() / 16.0f
                    val l = (b % 16 + 1).toFloat() / 16.0f
                    val m = (b / 16 + 1).toFloat() / 16.0f
                    val matrix4f2: Matrix4f = context.matrices.peek().getPositionMatrix()
                    val n = -0.001f
                    val vertexConsumer2: VertexConsumer = context.vertexConsumers.getBuffer(MAP_ICONS_RENDER_LAYER)
                    vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, k.toFloat() * -0.001f).color(255, 255, 255, 255).texture(g, h)
                        .light(light)
                        .next()
                    vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, k.toFloat() * -0.001f).color(255, 255, 255, 255).texture(l, h)
                        .light(light)
                        .next()
                    vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, k.toFloat() * -0.001f).color(255, 255, 255, 255).texture(l, m)
                        .light(light)
                        .next()
                    vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, k.toFloat() * -0.001f).color(255, 255, 255, 255).texture(g, m)
                        .light(light)
                        .next()
                    context.matrices.pop()
                    if (mapIcon.text() != null) {
                        val textRenderer = MinecraftClient.getInstance().textRenderer
                        val text = mapIcon.text()
                        val o = textRenderer.getWidth(text).toFloat()
                        val var10000 = 25.0f / o
                        Objects.requireNonNull(textRenderer)
                        val p = MathHelper.clamp(var10000, 0.0f, 6.0f / 9.0f)
                        context.matrices.push()

                        context.matrices.translate(this.x, this.y, 11.0)
                        context.matrices.scale(this.scale, this.scale, 1.0f)
                        context.matrices.translate(
                            mapStateData.mapState.centerX.toDouble() /*- offset*/ + mapIcon.x * mapScale / 2.0 + this.width / 2.0,
                            mapStateData.mapState.centerZ.toDouble() /*- offset*/ + (mapIcon.z + 1) * mapScale / 2.0 + this.height / 2.0,
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


    private fun getMapStates(stack: ItemStack?, world: World?): ArrayList<MapStateData> {
        val list = ArrayList<MapStateData>()
        if (world == null) return list

        val mapBookState = if (world.isClient) {
            MapBookStateManager.getClientMapBookState(getMapBookId(stack))
        } else {
            MapBookStateManager.getMapBookState(world.server!!, getMapBookId(stack))
        }

        if (mapBookState == null) return list

        for (i in mapBookState.mapIDs) {
            val mapState = FilledMapItem.getMapState(i, world)
            if (mapState != null) {
                if (world.dimensionKey.value.toString().equals(mapState.dimension.value.toString())) {
                    list.add(MapStateData(i, mapState))
                }
            }
        }
        return list
    }

    private fun getMapBookId(stack: ItemStack?): Int? {
        val nbtCompound = stack?.nbt
        return if (nbtCompound != null && nbtCompound.contains(
                "fixedminecraft:map_book",
                NbtElement.NUMBER_TYPE.toInt()
            )
        ) Integer.valueOf(nbtCompound.getInt("fixedminecraft:map_book")) else null
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
        var newZoom = if (scroll > 0) start - (start / (scroll * speed)) else (start * absScroll * speed) / (absScroll * speed - 1)
        newZoom = Math.min(Math.max(newZoom, 0.01f), 10f)// Math.clamp(newZoom, 0.01f, 10f)
        return newZoom
    }
}
