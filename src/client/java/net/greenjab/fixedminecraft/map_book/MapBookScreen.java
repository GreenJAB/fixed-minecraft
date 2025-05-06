package net.greenjab.fixedminecraft.map_book;

import net.greenjab.fixedminecraft.mixin.client.map.DrawContextAccessor;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/** Credit: Nettakrim */
public class MapBookScreen extends Screen {
    ItemStack item;
    public double x = 0.0;
    public double y = 0.0;
    public float scale = 1.0f;
    private float targetScale = 0.5f;

    public MapBookScreen(ItemStack item){
        super(item.getName());
        this.item = item;
    }

    @Override public void init() {
        if (client != null && client.player != null) {
            x = -client.player.getX();
            y = -client.player.getZ();
        }
        setScale(targetScale, width/2.0, height/2.0);
        for (MapStateData mapStateData : MapBookItem.getMapStates(item, client.world)) {
            addDrawable(new MapTile(this, mapStateData.id, mapStateData.mapState, client));
        }

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> { this.close();})
                .dimensions(width / 2 - 100, height -40, 200, 20).build());
    }

    @Override public boolean shouldPause() {
        return false;
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && Screen.hasShiftDown()) {
            int id = getMapBookId(item);
            if (id != -1) {
                MapBookPlayer marker = MapBookStateManager.INSTANCE.getClientMapBookState(getMapBookId(item)).marker;

                var pos = new Vec3d(mouseX, mouseY, 0.0);
                pos = pos.multiply((1 / scale));
                pos = pos.subtract(width / 2.0, height / 2.0, 0.0);
                pos = pos.subtract(this.x / scale, this.y / scale, 0.0);
                String dim = client.world.getDimensionEntry().getIdAsString();
                if (!marker.dimension.contains(dim) || (pos.distanceTo(new Vec3d(marker.x, marker.z, 0)) * scale)>5) {
                    MinecraftClient.getInstance().getNetworkHandler().sendCommand(String.format(
                            "mapBookMarker %s \"%s\" \"%s\" \"%s\"",
                            getMapBookId(item), pos.getX(), pos.getY(), dim));
                } else {
                    MinecraftClient.getInstance().getNetworkHandler().sendCommand(String.format(
                            "mapBookMarker %s \"%s\" \"%s\" \"%s\"",
                            getMapBookId(item), 0, 0, ""));
                }
            }
        }
        if (button == 2) {
            if (client.player.getAbilities().creativeMode) {
                var pos = new Vec3d(mouseX, mouseY, 0.0);
                pos = pos.multiply((1/scale));
                pos = pos.subtract(width / 2.0, height / 2.0, 0.0);
                pos = pos.subtract(this.x/scale, this.y/scale, 0.0);
                MinecraftClient.getInstance().getNetworkHandler().sendCommand(String.format(
                        "tp %.6f %.6f %.6f",
                        pos.getX(), client.player.getY(), pos.getY()));
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button < 2 && !Screen.hasShiftDown()) {
            x += deltaX;
            y += deltaY;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount != 0.0) {
            targetScale = zoom(scale, (float) -verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (context == null) return;

        if (scale != targetScale) {
            float newScale = MathHelper.lerp(delta, scale, targetScale);
            setScale(newScale, mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);

        PlayerEntity thisPlayer = client.player;
        if (thisPlayer==null)return;

        int id = getMapBookId(item);

        if (id != -1) {
            MapBookPlayer p = new MapBookPlayer();
            p.setPlayer(thisPlayer);
            ArrayList<MapBookPlayer> m = MapBookStateManager.INSTANCE.getClientMapBookState(id).players;
            if (m != null) {
                try {
                    for (MapBookPlayer player : m) {
                        if (player.dimension.contains(p.dimension)) {
                            if (!player.name.contains(p.name) ) {
                                renderPlayerIcon(context, player, false);
                            }
                        }
                    }
                } catch (ConcurrentModificationException ignored) {
                }
            }
            renderPlayerIcon(context, p, true);
            renderIcons(context);
            MapBookPlayer marker = MapBookStateManager.INSTANCE.getClientMapBookState(id).marker;
            if (marker.dimension.contains(p.dimension)) renderMarker(context, marker);
            renderPosition(context, mouseX, mouseY);
        }
    }

    private void renderPosition(DrawContext context, int mouseX, int mouseY) {

        var pos = new Vec3d(mouseX, mouseY, 0.0);
        pos = pos.multiply((1/scale));
        pos = pos.subtract(width / 2.0, height / 2.0, 0.0);
        pos = pos.subtract(this.x/scale, this.y/scale, 0.0);


        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String text = (int)pos.getX() + ", " + (int)pos.getY();
        float o = textRenderer.getWidth(text);
        Objects.requireNonNull(textRenderer);
        MatrixStack matrix = context.getMatrices();
        matrix.push();

        matrix.translate(width / 2.0, height -60.0, 20.0);

        matrix.translate(-o / 2f, 8.0f, 0.1f);

        textRenderer.draw(
                text,
                0.0f,
                0.0f,
                -1,
                false,
                matrix.peek().getPositionMatrix(),
                ((DrawContextAccessor)context).getVertexConsumers(),
                TextLayerType.NORMAL,
                Integer.MIN_VALUE,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
        );
        matrix.pop();

    }

    private void renderPlayerIcon(DrawContext context, MapBookPlayer player, boolean thisPlayer) {
        float x = (float) player.x;
        float z = (float) player.z;
        float rotation = player.yaw;
        MatrixStack matrix = context.getMatrices();

        matrix.push();
        matrix.translate(this.x, this.y, 0.0);
        matrix.scale(this.scale, this.scale, 1.0f);
        matrix.translate(x + width/ 2.0, z + height / 2.0, 0.0);
        matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
        matrix.scale(8.0f, 8.0f, -3.0f);
        matrix.translate(0f, 0f, thisPlayer?-12.0f:-11.0f);
        matrix.scale(1f / this.scale, 1f / this.scale, 1.0f);
        Sprite sprite = client.getMapDecorationsAtlasManager().getSprite(
                new MapDecoration(
                        MapDecorationTypes.PLAYER,
                        (byte) 0,
                        (byte) 0,
                        (byte) 0,
                        Optional.empty()
                )
        );
        float g = sprite.getMinU();
        float h = sprite.getMinV();
        float l = sprite.getMaxU();
        float m = sprite.getMaxV();
        Matrix4f matrix4f2 = matrix.peek().getPositionMatrix();
        VertexConsumer vertexConsumer2 = ((DrawContextAccessor)context).getVertexConsumers().getBuffer(RenderLayer.getText(sprite.getAtlasId()));
        vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(g, h).light(15728880);
        vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(l, h).light(15728880);
        vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(l, m).light(15728880);
        vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(g, m).light(15728880);
        matrix.pop();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String text = player.name;
        float o = textRenderer.getWidth(text);
        Objects.requireNonNull(textRenderer);
        matrix.push();

        matrix.translate(this.x, this.y, 15.0);
        matrix.scale(this.scale, this.scale, 1.0f);

        matrix.translate(x + width / 2.0, z + height / 2.0, 0.0);

        matrix.scale(1 / this.scale, 1 / this.scale, 1.0f);
        matrix.translate(-o / 2f, 8.0f, thisPlayer?12.0f:11.0f);

        textRenderer.draw(
                text,
                0.0f,
                0.0f,
                -1,
                false,
                matrix.peek().getPositionMatrix(),
                ((DrawContextAccessor) context).getVertexConsumers(),
                TextLayerType.NORMAL,
                Integer.MIN_VALUE,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
        );
        matrix.pop();
    }

    private void renderIcons(DrawContext context) {
        int light = LightmapTextureManager.MAX_LIGHT_COORDINATE;


        for (MapStateData mapStateData : getMapStates(item, client.world)) {
            double render = 0.0;
            if (client.world.getDimensionEntry().getIdAsString().contains(mapStateData.mapState.dimension.getValue().toString()))
                render = 1.0;
            if (client.world.getDimensionEntry().getIdAsString().contains("the_nether") && mapStateData.mapState.dimension.getValue().toString().contains("overworld"))
                render = 1/8.0;
            if (render>0) {
                Iterator<MapDecoration>  var11 = mapStateData.mapState.getDecorations().iterator();
                MatrixStack matrix = context.getMatrices();
                while (var11.hasNext()) {
                    MapDecoration mapIcon = var11.next();
                    if (!mapIcon.type().getIdAsString().contains("player")) {
                        matrix.push();
                        matrix.translate(this.x, this.y, 0.0);
                        matrix.scale(this.scale, this.scale, 1.0f);
                        float mapScale = (float) Math.pow(2, mapStateData.mapState.scale);
                        float offset = 64f * mapScale;
                        double x = (mapStateData.mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2) * render;
                        double z = (mapStateData.mapState.centerZ - offset + (mapIcon.z() + 128 + 1) * mapScale / 2) * render;
                        matrix.translate(x + width / 2.0, z + height / 2.0, 0.0);
                        matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180F));
                        matrix.scale(8.0f, 8.0f, -3.0f);
                        matrix.translate(0f, 0f, -10.0f);
                        matrix.scale(1f / this.scale, 1f / this.scale, 1.0f);
                        Sprite sprite = client.getMapDecorationsAtlasManager().getSprite(
                                new MapDecoration(
                                        mapIcon.type(),
                                        (byte) 0,
                                        (byte) 0,
                                        (byte) 0,
                                        Optional.empty()
                                )
                        );
                        float g = sprite.getMinU();
                        float h = sprite.getMinV();
                        float l = sprite.getMaxU();
                        float m = sprite.getMaxV();
                        Matrix4f matrix4f2 = matrix.peek().getPositionMatrix();
                        VertexConsumer vertexConsumer2 =
                                ((DrawContextAccessor)context).getVertexConsumers().getBuffer(RenderLayer.getText(sprite.getAtlasId()));
                        vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(g, h).light(15728880);
                        vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(l, h).light(15728880);
                        vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(l, m).light(15728880);
                        vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(g, m).light(15728880);
                        matrix.pop();


                        if (mapIcon.name().isPresent()) {
                            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                            Text text = mapIcon.name().get();
                            float o = textRenderer.getWidth(text);
                            Objects.requireNonNull(textRenderer);
                            matrix.push();
                            matrix.translate(this.x, this.y, 11.0);
                            matrix.scale(this.scale, this.scale, 1.0f);
                            double mapx = (mapStateData.mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2) * render;
                            double mapz = (mapStateData.mapState.centerZ - offset + (mapIcon.z() + 128 + 1) * mapScale / 2) * render;
                            matrix.translate(mapx + width / 2.0, mapz + height / 2.0, 0.0);
                            matrix.scale(1 / this.scale, 1 / this.scale, 1.0f);
                            matrix.translate(-o / 2f, 8.0f, 0.1f);

                            textRenderer.draw(
                                    text,
                                    0.0f,
                                    0.0f,
                                    -1,
                                    false,
                                    matrix.peek().getPositionMatrix(),
                                    ((DrawContextAccessor) context).getVertexConsumers(),
                                    TextLayerType.NORMAL,
                                    Integer.MIN_VALUE,
                                    light
                            );
                            matrix.pop();
                        }
                    }
                }
            }
        }
    }

    private void renderMarker(DrawContext context, MapBookPlayer player) {
        float x = (float) player.x;
        float z = (float) player.z;
        float rotation = player.yaw;
        MatrixStack matrix = context.getMatrices();

        matrix.push();
        matrix.translate(this.x, this.y, 0.0);
        matrix.scale(this.scale, this.scale, 1.0f);
        matrix.translate(x + width/ 2.0, z + height / 2.0, 0.0);
        matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
        matrix.scale(8.0f, 8.0f, -3.0f);
        matrix.translate(0f, 0f, -10.5f);
        matrix.scale(1f / this.scale, 1f / this.scale, 1.0f);
        Sprite sprite = client.getMapDecorationsAtlasManager().getSprite(
                new MapDecoration(
                        MapDecorationTypes.TARGET_X,
                        (byte) 0,
                        (byte) 0,
                        (byte) 0,
                        Optional.empty()
                )
        );
        float g = sprite.getMinU();
        float h = sprite.getMinV();
        float l = sprite.getMaxU();
        float m = sprite.getMaxV();
        Matrix4f matrix4f2 = matrix.peek().getPositionMatrix();
        VertexConsumer vertexConsumer2 = ((DrawContextAccessor)context).getVertexConsumers().getBuffer(RenderLayer.getText(sprite.getAtlasId()));
        vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(g, h).light(15728880);
        vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, -0.1f).color(255, 255, 255, 255).texture(l, h).light(15728880);
        vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(l, m).light(15728880);
        vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, -0.1f).color(255, 255, 255, 255).texture(g, m).light(15728880);
        matrix.pop();

        matrix.pop();
    }


    private ArrayList<MapStateData> getMapStates(ItemStack stack, World world) {
        ArrayList<MapStateData> list = new ArrayList<>();
        MapBookState mapBookState = getMapBookState(stack, world);

        if (mapBookState != null) {
            for (int i : mapBookState.mapIDs) {
                MapState mapState = world.getMapState(new MapIdComponent(i));
                if (mapState != null) {
                    list.add(new MapStateData(new MapIdComponent(i), mapState));
                }
            }
        }
        return list;
    }

    private MapBookState getMapBookState(ItemStack stack, World world) {
        int id = getMapBookId(stack) ;
        if (id == -1) return null;
        return MapBookStateManager.INSTANCE.getClientMapBookState(id);
    }

    private static int getMapBookId(ItemStack stack) {
        MapIdComponent mapIdComponent = stack.getOrDefault(DataComponentTypes.MAP_ID, null);
        if (mapIdComponent!=null) return mapIdComponent.id();
        return -1;
    }

    private void setScale(float newScale, double mouseX, double mouseY) {
        double offsetX = x-mouseX;
        double offsetY = y-mouseY;

        double scaleChange = newScale/scale;

        x = (scaleChange * offsetX)+mouseX;
        y = (scaleChange * offsetY)+mouseY;

        scale = newScale;
    }

    private float zoom(float start, float scroll) {
        // logarithmic zoom that doesn't drift when zooming in and out repeatedly
        float absScroll = Math.abs(scroll);
        float speed = 5.0f;
        float newZoom =  scroll > 0 ? start - (start / (scroll * speed)) : (start * absScroll * speed) / (absScroll * speed - 1);
        newZoom = Math.min(Math.max(newZoom, 0.005f), 10f);
        return newZoom;
    }
}
