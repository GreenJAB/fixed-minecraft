package net.greenjab.fixedminecraft.screens;

import net.greenjab.fixedminecraft.map_book.MapTile;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;

/** Credit: Nettakrim */
public class MapBookScreen extends Screen {
    ItemStack item;
    public float x = 0.0f;
    public float y = 0.0f;
    public float scale = 1.0f;
    private float targetScale = 0.5f;

    public MapBookScreen(ItemStack item){
        super(item.getDisplayName());
        this.item = item;
    }

    @Override public void init() {
        if (minecraft.player != null) {
            x = (float) -minecraft.player.getX();
            y = (float) -minecraft.player.getZ();
        }
        setScale(targetScale, width/2.0f, height/2.0f);

        for (int i = 4;i >= 0;i--) {
            for (MapStateData mapStateData : MapBookItem.getMapStates(item, minecraft.level)) {
                if (mapStateData.mapState.scale == i) addRenderableOnly(new MapTile(this, mapStateData.id, mapStateData.mapState, minecraft));
            }
        }

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, _ -> this.onClose())
                .bounds(width / 2 - 100, height -40, 200, 20).build());
    }

    @Override public boolean isPauseScreen() {
        return false;
    }

    @Override public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
        assert minecraft.level != null;
        assert minecraft.player != null;
        assert Minecraft.getInstance().getConnection() != null;
        if (click.button() == 0 && minecraft.hasShiftDown()) {
            int id = getMapBookId(item);
            if (id != -1) {
                MapBookPlayer marker = MapBookStateManager.INSTANCE.getClientMapBookState(getMapBookId(item)).marker;

                var pos = new Vec3(click.x(), click.y(), 0.0);
                pos = pos.scale((1 / scale));
                pos = pos.subtract(width / 2.0, height / 2.0, 0.0);
                pos = pos.subtract(this.x / scale, this.y / scale, 0.0);
                String dim = minecraft.level.dimension().identifier().toString();
                if (!marker.dimension.contains(dim) || (pos.distanceTo(new Vec3(marker.x, marker.z, 0)) * scale)>5) {
                    Minecraft.getInstance().getConnection().sendCommand(String.format(
                            "mapBookMarker %s \"%s\" \"%s\" \"%s\"",
                            getMapBookId(item), pos.x(), pos.y(), dim));
                } else {
                    Minecraft.getInstance().getConnection().sendCommand(String.format(
                            "mapBookMarker %s \"%s\" \"%s\" \"%s\"",
                            getMapBookId(item), 0, 0, ""));
                }
            }
        }
        if (click.button() == 2) {
            if (minecraft.player.isCreative()) {
                var pos = new Vec3(click.x(), click.y(), 0.0);
                pos = pos.scale((1/scale));
                pos = pos.subtract(width / 2.0, height / 2.0, 0.0);
                pos = pos.subtract(this.x/scale, this.y/scale, 0.0);
                Minecraft.getInstance().getConnection().sendCommand(String.format(
                        "tp %.6f %.6f %.6f",
                        pos.x(), minecraft.player.getY(), pos.y()));
            }
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
        if (click.button() < 2 && !minecraft.hasShiftDown()) {
            x += (float) deltaX;
            y += (float) deltaY;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount != 0.0) {
            targetScale = zoom(scale, (float) -verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {

        if (scale != targetScale) {
            float newScale = Mth.lerp(delta, scale, targetScale);
            setScale(newScale, mouseX, mouseY);
        }

        super.extractRenderState(context, mouseX, mouseY, delta);

        Player thisPlayer = minecraft.player;
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

    private void renderPosition(GuiGraphicsExtractor context, int mouseX, int mouseY) {

        var pos = new Vec3(mouseX, mouseY, 0.0);
        pos = pos.scale((1/scale));
        pos = pos.subtract(width / 2.0, height / 2.0, 0.0);
        pos = pos.subtract(this.x/scale, this.y/scale, 0.0);


        Font textRenderer = Minecraft.getInstance().font;
        String text = (int)pos.x() + ", " + (int)pos.y();
        int o = textRenderer.width(text);
        Objects.requireNonNull(textRenderer);
        Matrix3x2fStack matrix = context.pose();
        matrix.pushMatrix();
        matrix.translate((int)((width / 2.0f) -o / 2f), (int)(height -60.0f + 8f));
        context.fill(- 1, - 1, o, 9 , (new Color(50, 50, 50, 150)).hashCode());
        context.text(textRenderer, text, 0, 0, -1, true);
        matrix.popMatrix();

    }

    private void renderPlayerIcon(GuiGraphicsExtractor context, MapBookPlayer player, boolean thisPlayer) {
        Minecraft minecraft = Minecraft.getInstance();
        float x = (float) player.x;
        float z = (float) player.z;
        float rotation = player.yaw;
        Matrix3x2fStack matrix = context.pose();

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
            context.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.parse(
                            //"hud/locator_bar_dot/map_decorations/" + mapIcon.getAssetId().getPath()),
                            "hud/locator_bar_dot/map_decorations/player"),
                    0, 0, 1, 1, -1);
        } else {
            int color = getColor(player, minecraft);
            context.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.parse(
                            "hud/locator_bar_dot/map_decorations/player"),
                    0, 0, 1, 1, color);
        }
        matrix.popMatrix();

        Font textRenderer = minecraft.font;
        String text = player.name;
        int o = textRenderer.width(text);
        matrix.pushMatrix();

        matrix.translate(this.x, this.y);
        matrix.scale(this.scale, this.scale);
        matrix.translate(x + width / 2.0f, z + height / 2.0f);
        matrix.scale(1 / this.scale, 1 / this.scale);
        matrix.translate(-o / 2f, 10.0f);

        context.fill(- 1, - 1, o, 9, (new Color(50, 50, 50, 150)).hashCode());
        context.text(textRenderer, text, 0, 0, -1, true);
        matrix.popMatrix();

    }

    public static int getColor(MapBookPlayer player, Minecraft minecraft) {
        assert minecraft.player != null;
        int color = ARGB.setBrightness(ARGB.color(255, player.name.hashCode()), 0.9F);
        for (PlayerInfo playerListEntry : minecraft.player.connection.getOnlinePlayers()) {
            if (Objects.equals(playerListEntry.getProfile().name(), player.name)) {
                Team team = playerListEntry.getTeam();
                if (team != null) {
                    ChatFormatting formatting = team.getColor();
                    if (formatting.isColor()) {
                        assert formatting.getColor() != null;
                        color = (new Color(formatting.getColor()).hashCode());
                    }
                }
            }
        }
        return color;
    }

    private void renderIcons(GuiGraphicsExtractor context) {
        assert minecraft.level != null;
        for (MapStateData mapStateData : getMapStates(item, minecraft.level)) {
            float render = 0.0f;
            if (minecraft.level.dimension().identifier().toString().contains(mapStateData.mapState.dimension.identifier().toString()))
                render = 1.0f;
            if (minecraft.level.dimension().identifier().toString().contains("the_nether") && mapStateData.mapState.dimension.identifier().toString().contains("overworld"))
                render = 1/8.0f;
            if (render>0) {
                Iterator<MapDecoration>  var11 = mapStateData.mapState.getDecorations().iterator();
                Matrix3x2fStack matrix = context.pose();
                while (var11.hasNext()) {
                    MapDecoration mapIcon = var11.next();
                    if (!mapIcon.type().getRegisteredName().contains("player")) {
                        matrix.pushMatrix();
                        matrix.translate(this.x, this.y);
                        matrix.scale(this.scale, this.scale);
                        float mapScale = (float) Math.pow(2, mapStateData.mapState.scale);
                        float offset = 64f * mapScale;
                        float x = (mapStateData.mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2) * render;
                        float z = (mapStateData.mapState.centerZ - offset + (mapIcon.y() + 128 + 1) * mapScale / 2) * render;
                        matrix.translate(x + width / 2.0f, z + height / 2.0f);
                        matrix.scale(8.0f, 8.0f);
                        matrix.scale(2.5f, 2.5f);
                        matrix.scale(1f / this.scale, 1f / this.scale);
                        matrix.translate(-0.5f, -0.5f);

                        context.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.parse(
                                        "hud/locator_bar_dot/map_decorations/" + mapIcon.getSpriteLocation().getPath()),
                                0, 0, 1, 1, -1);
                        matrix.popMatrix();

                        if (mapIcon.name().isPresent()) {

                            Font textRenderer = Minecraft.getInstance().font;
                            Component text = mapIcon.name().get();
                            int o = textRenderer.width(text);
                            matrix.pushMatrix();

                            matrix.translate(this.x, this.y);
                            matrix.scale(this.scale, this.scale);
                            float mapx = (mapStateData.mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2) * render;
                            float mapz = (mapStateData.mapState.centerZ - offset + (mapIcon.y() + 128 + 1) * mapScale / 2) * render;
                            matrix.translate(mapx + width / 2.0f, mapz + height / 2.0f);
                            matrix.scale(1 / this.scale, 1 / this.scale);
                            matrix.translate(-o / 2f, 10.0f);

                            context.fill(- 1, - 1, o, 9, (new Color(50, 50, 50, 150)).hashCode());
                            context.text(textRenderer, text, 0, 0, -1, true);
                            matrix.popMatrix();
                        }
                    }
                }
            }
        }
    }

    private void renderMarker(GuiGraphicsExtractor context, MapBookPlayer player) {
        float x = (float) player.x;
        float z = (float) player.z;
        float rotation = player.yaw;
        Matrix3x2fStack matrix = context.pose();

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

        context.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.parse(
                        "hud/locator_bar_dot/map_decorations/target_x"),
                0, 0, 1, 1, -1);
        matrix.popMatrix();
    }


    private ArrayList<MapStateData> getMapStates(ItemStack stack, Level level) {
        ArrayList<MapStateData> list = new ArrayList<>();
        MapBookState mapBookState = getMapBookState(stack);

        if (mapBookState != null) {
            for (int i : mapBookState.mapIDs) {
                MapItemSavedData mapState = level.getMapData(new MapId(i));
                if (mapState != null) {
                    list.add(new MapStateData(new MapId(i), mapState));
                }
            }
        }
        return list;
    }

    private MapBookState getMapBookState(ItemStack stack) {
        int id = getMapBookId(stack) ;
        if (id == -1) return null;
        return MapBookStateManager.INSTANCE.getClientMapBookState(id);
    }

    private static int getMapBookId(ItemStack stack) {
        MapId mapIdComponent = stack.getOrDefault(DataComponents.MAP_ID, new MapId(0));
        return mapIdComponent.id();
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
        newZoom = Mth.clamp(newZoom, 0.005f, 10f);
        return newZoom;
    }
}
