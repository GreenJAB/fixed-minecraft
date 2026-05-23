package net.greenjab.fixedminecraft.map_book;

import net.greenjab.fixedminecraft.screens.MapBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix3x2fStack;

/** Credit: Nettakrim */
public class MapTile implements Renderable {
    private final MapBookScreen screen;
    MapId id;
    private final MapItemSavedData mapState;

    private final MapRenderState mapRenderState = new MapRenderState();

    public MapTile(MapBookScreen screen, MapId id, MapItemSavedData mapState, Minecraft client) {
        this.screen = screen;
        this.id = id;
        this.mapState = mapState;
        client.getMapRenderer().extractRenderState(id, mapState, mapRenderState);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        float mapScale = (float) Math.pow(2, mapState.scale);
        float offset = 64f * mapScale;

        Matrix3x2fStack matrix = context.pose();

        matrix.pushMatrix();
        //for (int i = 0; i < 4-mapState.scale;i++)context.goUpLayer();
        matrix.translate(screen.x, screen.y);
        matrix.scale(screen.scale, screen.scale);
        matrix.translate(
                (float) (mapState.centerX - offset + screen.width / 2.0),
                (float) (mapState.centerZ - offset + screen.height / 2.0));
        matrix.scale(mapScale, mapScale);
        context.map(mapRenderState);
        //for (int i = 0; i < 4-mapState.scale;i++)context.popLayer();
        matrix.popMatrix();
    }
}
