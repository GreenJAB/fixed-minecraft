package net.greenjab.fixedminecraft.map_book;

import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/** Credit: Nettakrim */
public class MapBookScreen extends Screen {
    ItemStack item;
    public float x = 0.0f;
    public float y = 0.0f;
    public float scale = 1.0f;
    private float targetScale = 0.5f;

    public MapBookScreen(ItemStack item){
        super(item.getName());
        this.item = item;
    }

    @Override public void init() {
        if (client != null && client.player != null) {
            x = (float) -client.player.getX();
            y = (float) -client.player.getZ();
        }
        setScale(targetScale, width/2.0f, height/2.0f);

        for (int i = 4;i >= 0;i--) {
            for (MapStateData mapStateData : MapBookItem.getMapStates(item, client.world)) {
                if (mapStateData.mapState.scale == i) addDrawable(new MapTile(this, mapStateData.id, mapStateData.mapState, client));
            }
        }

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> { this.close();})
                .dimensions(width / 2 - 100, height -40, 200, 20).build());
    }

    @Override public boolean shouldPause() {
        return false;
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button, boolean doubleClick) {
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
                    MinecraftClient.getInstance().getNetworkHandler().sendChatCommand(String.format(
                            "mapBookMarker %s \"%s\" \"%s\" \"%s\"",
                            getMapBookId(item), pos.getX(), pos.getY(), dim));
                } else {
                    MinecraftClient.getInstance().getNetworkHandler().sendChatCommand(String.format(
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
                MinecraftClient.getInstance().getNetworkHandler().sendChatCommand(String.format(
                        "tp %.6f %.6f %.6f",
                        pos.getX(), client.player.getY(), pos.getY()));
            }
        }
        return super.mouseClicked(mouseX, mouseY, button, doubleClick);
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
            renderIcons(context);
            MapBookPlayer p = new MapBookPlayer();
            p.setPlayer(thisPlayer);

            MapBookPlayer marker = MapBookStateManager.INSTANCE.getClientMapBookState(id).marker;
            if (marker.dimension.contains(p.dimension)) renderMarker(context, marker);

            ArrayList<MapBookPlayer> m = MapBookStateManager.INSTANCE.getClientMapBookState(id).players;
            if (m != null) {
                try {
                    for (MapBookPlayer player : m) {
                        if (player.dimension.contains(p.dimension)) {
                            if (!(player.name.contains(p.name) && p.name.contains(player.name))) {
                                renderPlayerIcon(context, player, false);
                            }
                        }
                    }
                } catch (ConcurrentModificationException ignored) {
                }
            }
            renderPlayerIcon(context, p, true);
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
        int o = textRenderer.getWidth(text);
        Objects.requireNonNull(textRenderer);
        Matrix3x2fStack matrix = context.getMatrices();
        matrix.pushMatrix();
        matrix.translate((int)((width / 2.0f) -o / 2f), (int)(height -60.0f + 8f));
        context.fill(- 1, - 1, o, 9 , (new Color(50, 50, 50, 150)).hashCode());
        context.drawText(textRenderer, text, 0, 0, -1, true);
        matrix.popMatrix();

    }

    private void renderPlayerIcon(DrawContext context, MapBookPlayer player, boolean thisPlayer) {

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        float x = (float) player.x;
        float z = (float) player.z;
        float rotation = player.yaw;
        Matrix3x2fStack matrix = context.getMatrices();

        matrix.pushMatrix();
        matrix.translate(this.x, this.y);
        matrix.scale(this.scale, this.scale);
        matrix.translate(x + width/ 2.0f, z + height / 2.0f);
        matrix.rotate((float) (rotation * Math.PI/180.0f));
        matrix.rotate((float) (Math.PI));
        matrix.scale(8.0f, 8.0f);
        matrix.scale(2.5f, 2.5f);
        matrix.scale(1f / this.scale, 1f / this.scale);
        matrix.translate(-0.5f, -0.5f);

        if (thisPlayer) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(
                            //"hud/locator_bar_dot/map_decorations/" + mapIcon.getAssetId().getPath()),
                            "hud/locator_bar_dot/map_decorations/player"),
                    0, 0, 1, 1, -1);
        } else {
            int color = ColorHelper.withBrightness(ColorHelper.withAlpha(255, player.name.hashCode()), 0.9F);
            for (PlayerListEntry playerListEntry : client.player.networkHandler.getPlayerList()) {
                if (Objects.equals(playerListEntry.getProfile().getName(), player.name)) {
                    Team team = playerListEntry.getScoreboardTeam();
                    if (team != null) {
                        Formatting formatting = team.getColor();
                        if (formatting.isColor()) {
                            color = (new Color(formatting.getColorValue().intValue()).hashCode());
                        }
                    }
                }
            }
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(
                            "hud/locator_bar_dot/map_decorations/player"),
                    0, 0, 1, 1, color);
        }
        matrix.popMatrix();

        TextRenderer textRenderer = minecraftClient.textRenderer;
        String text = player.name;
        int o = textRenderer.getWidth(text);
        matrix.pushMatrix();

        matrix.translate(this.x, this.y);
        matrix.scale(this.scale, this.scale);
        matrix.translate(x + width / 2.0f, z + height / 2.0f);
        matrix.scale(1 / this.scale, 1 / this.scale);
        matrix.translate(-o / 2f, 10.0f);

        context.fill(- 1, - 1, o, 9, (new Color(50, 50, 50, 150)).hashCode());
        context.drawText(textRenderer, text, 0, 0, -1, true);
        matrix.popMatrix();

    }

    private void renderIcons(DrawContext context) {

        for (MapStateData mapStateData : getMapStates(item, client.world)) {
            float render = 0.0f;
            if (client.world.getDimensionEntry().getIdAsString().contains(mapStateData.mapState.dimension.getValue().toString()))
                render = 1.0f;
            if (client.world.getDimensionEntry().getIdAsString().contains("the_nether") && mapStateData.mapState.dimension.getValue().toString().contains("overworld"))
                render = 1/8.0f;
            if (render>0) {
                Iterator<MapDecoration>  var11 = mapStateData.mapState.getDecorations().iterator();
                Matrix3x2fStack matrix = context.getMatrices();
                while (var11.hasNext()) {
                    MapDecoration mapIcon = var11.next();
                    if (!mapIcon.type().getIdAsString().contains("player")) {
                        matrix.pushMatrix();
                        matrix.translate(this.x, this.y);
                        matrix.scale(this.scale, this.scale);
                        float mapScale = (float) Math.pow(2, mapStateData.mapState.scale);
                        float offset = 64f * mapScale;
                        float x = (mapStateData.mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2) * render;
                        float z = (mapStateData.mapState.centerZ - offset + (mapIcon.z() + 128 + 1) * mapScale / 2) * render;
                        matrix.translate(x + width / 2.0f, z + height / 2.0f);
                        matrix.scale(8.0f, 8.0f);
                        matrix.scale(2.5f, 2.5f);
                        matrix.scale(1f / this.scale, 1f / this.scale);
                        matrix.translate(-0.5f, -0.5f);

                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(
                                        "hud/locator_bar_dot/map_decorations/" + mapIcon.getAssetId().getPath()),
                                0, 0, 1, 1, -1);
                        matrix.popMatrix();

                        if (mapIcon.name().isPresent()) {

                            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                            Text text = mapIcon.name().get();
                            int o = textRenderer.getWidth(text);
                            matrix.pushMatrix();

                            matrix.translate(this.x, this.y);
                            matrix.scale(this.scale, this.scale);
                            float mapx = (mapStateData.mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2) * render;
                            float mapz = (mapStateData.mapState.centerZ - offset + (mapIcon.z() + 128 + 1) * mapScale / 2) * render;
                            matrix.translate(mapx + width / 2.0f, mapz + height / 2.0f);
                            matrix.scale(1 / this.scale, 1 / this.scale);
                            matrix.translate(-o / 2f, 10.0f);

                            context.fill(- 1, - 1, o, 9, (new Color(50, 50, 50, 150)).hashCode());
                            context.drawText(textRenderer, text, 0, 0, -1, true);
                            matrix.popMatrix();
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
        Matrix3x2fStack matrix = context.getMatrices();

        matrix.pushMatrix();
        matrix.translate(this.x, this.y);
        matrix.scale(this.scale, this.scale);
        matrix.translate(x + width/ 2.0f, z + height / 2.0f);
        matrix.rotate((float) (rotation * Math.PI/180.0f));
        matrix.rotate((float) (Math.PI));
        matrix.scale(8.0f, 8.0f);
        matrix.scale(2.5f, 2.5f);
        matrix.scale(1f / this.scale, 1f / this.scale);
        matrix.translate(-0.5f, -0.5f);

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(
                        "hud/locator_bar_dot/map_decorations/target_x"),
                0, 0, 1, 1, -1);
        matrix.popMatrix();
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

    private void setScale(float newScale, float mouseX, float mouseY) {
        float offsetX = x-mouseX;
        float offsetY = y-mouseY;

        float scaleChange = newScale/scale;

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
