package net.greenjab.fixedminecraft.map_book;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapState;
import org.joml.Matrix3x2fStack;

/** Credit: Nettakrim */
public class MapTile implements Drawable {
    private final MapBookScreen screen;
    MapIdComponent id;
    private final MapState mapState;

    private final MapRenderState mapRenderState = new MapRenderState();

    public MapTile(MapBookScreen screen, MapIdComponent id, MapState mapState, MinecraftClient client) {
        this.screen = screen;
        this.id = id;
        this.mapState = mapState;
        client.getMapRenderer().update(id, mapState, mapRenderState);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float mapScale = (float) Math.pow(2, mapState.scale);
        float offset = 64f * mapScale;

        Matrix3x2fStack matrix = context.getMatrices();

        context.getMatrices().pushMatrix();
        //for (int i = 0; i < 4-mapState.scale;i++)context.goUpLayer();
        matrix.translate(screen.x, screen.y);
        matrix.scale(screen.scale, screen.scale);
        matrix.translate(
                (float) (mapState.centerX - offset + screen.width / 2.0),
                (float) (mapState.centerZ - offset + screen.height / 2.0));
        matrix.scale(mapScale, mapScale);
        context.drawMap(mapRenderState);
        //for (int i = 0; i < 4-mapState.scale;i++)context.popLayer();
        matrix.popMatrix();
    }
}
