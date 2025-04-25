package net.greenjab.fixedminecraft.map_book;


import net.greenjab.fixedminecraft.mixin.client.map.DrawContextAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapState;
import org.joml.Matrix3x2fStack;

/** Credit: Nettakrim */
public class MapTile implements Drawable {
    private final MapBookScreen screen;
    MapIdComponent id;
    private final MapState mapState;
    private final MinecraftClient client;

    private final MapRenderState mapRenderState = new MapRenderState();

    public MapTile(MapBookScreen screen, MapIdComponent id, MapState mapState, MinecraftClient client) {
        this.screen = screen;
        this.id = id;
        this.mapState = mapState;
        this.client = client;
        client.getMapRenderer().update(id, mapState, mapRenderState);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float mapScale = (float) Math.pow(2, mapState.scale);
        float offset = 64f * mapScale;
        /*MatrixStack matrix = context.getMatrices();
        context.getMatrices().push();
        matrix.translate(screen.x, screen.y, 1.0 / (mapState.scale + 1.0) + 1.0);
        matrix.scale(screen.scale, screen.scale, -1.0f);
        matrix.translate(
                mapState.centerX - offset + screen.width / 2.0,
                mapState.centerZ - offset + screen.height / 2.0, 0.0
        );
        matrix.scale(mapScale, mapScale, 1.0f);
        client.getMapRenderer().draw(mapRenderState, matrix, ((DrawContextAccessor)context).getVertexConsumers(), true, 15728880);
        matrix.pop();*/

        Matrix3x2fStack matrix = context.getMatrices();

        context.getMatrices().pushMatrix();
        matrix.translate((float) screen.x, (float) screen.y);
        matrix.scale(screen.scale, screen.scale);
        matrix.translate(
                (float) (mapState.centerX - offset + screen.width / 2.0),
                (float) (mapState.centerZ - offset + screen.height / 2.0));
        matrix.scale(mapScale, mapScale);
        context.drawMap(mapRenderState);
        //client.getMapRenderer().draw(mapRenderState, matrix, ((DrawContextAccessor)context).getVertexConsumers(), true, 15728880);
        matrix.popMatrix();
    }
}
