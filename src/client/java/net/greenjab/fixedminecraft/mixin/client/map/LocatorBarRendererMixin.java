package net.greenjab.fixedminecraft.mixin.client.map;

import com.mojang.blaze3d.platform.Window;
import net.greenjab.fixedminecraft.screens.MapBookScreen;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.PartialTickSupplier;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import static net.minecraft.world.item.MapItem.getSavedData;


@Mixin(LocatorBarRenderer.class)
public abstract class LocatorBarRendererMixin {

    @Shadow
    @Final
    private static Identifier LOCATOR_BAR_ARROW_DOWN;
    @Shadow
    @Final
    private static Identifier LOCATOR_BAR_ARROW_UP;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractRenderState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;"
    ), cancellable = true
    )
    private void addBannerMarkers(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci){
        Minecraft minecraft = Minecraft.getInstance();
        int top = getCenterY(minecraft.getWindow());
        assert minecraft.level != null;
        assert minecraft.player != null;
        assert minecraft.getCameraEntity() != null;
        Entity cameraEntity = this.minecraft.getCameraEntity();
        Level level = cameraEntity.level();
        TickRateManager tickRateManager = level.tickRateManager();
        PartialTickSupplier partialTickSupplier = entity -> deltaTracker.getGameTimeDeltaPartialTick(
                !tickRateManager.isEntityFrozen(entity)
        );
        minecraft.player.connection
                .getWaypointManager()
                .forEachWaypoint(cameraEntity, waypoint -> {
            if (!(Boolean)waypoint.id().left().map((uuid) -> uuid.equals(minecraft.getCameraEntity().getUUID())).orElse(false)) {
                double angle = waypoint.yawAngleToCamera(level, this.minecraft.gameRenderer.getMainCamera(), partialTickSupplier);
                if (!(angle <= -60.0) && !(angle > 60.0)) {
                    int screenMiddle = Mth.ceil((graphics.guiWidth() - 9) / 2.0F);
                    Waypoint.Icon icon = waypoint.icon();
                    WaypointStyle style = this.minecraft.getWaypointStyles().get(icon.style);
                    float distance = Mth.sqrt((float)waypoint.distanceSquared(cameraEntity));
                    Identifier sprite = style.sprite(distance);
                    int color = icon.color
                            .orElseGet(
                                    /* lambda$extractRenderState$3 */ () -> waypoint.id()
                                            .map(
                                                    /* lambda$extractRenderState$4 */ uuid -> ARGB.setBrightness(ARGB.color(255, uuid.hashCode()), 0.9F),
                                                    /* lambda$extractRenderState$5 */ name -> ARGB.setBrightness(ARGB.color(255, name.hashCode()), 0.9F)
                                            )
                            );
                    int dotPosition = Mth.floor(angle * 173.0 / 2.0 / 60.0);
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, screenMiddle + dotPosition, top - 2, 9, 9, color);
                    TrackedWaypoint.PitchDirection pitchDirection = waypoint.pitchDirectionToCamera(level, this.minecraft.gameRenderer, partialTickSupplier);
                    if (pitchDirection != TrackedWaypoint.PitchDirection.NONE) {
                        int arrowTop;
                        Identifier arrowSprite;
                        if (pitchDirection == TrackedWaypoint.PitchDirection.DOWN) {
                            arrowTop = 6;
                            arrowSprite = LOCATOR_BAR_ARROW_DOWN;
                        } else {
                            arrowTop = -6;
                            arrowSprite = LOCATOR_BAR_ARROW_UP;
                        }

                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, arrowSprite, screenMiddle + dotPosition + 1, top + arrowTop, 7, 5);
                    }
                }
            }
        });


        ItemStack stack = minecraft.player.getMainHandItem();
        if (!(stack.getItem() instanceof MapBookItem)) stack = minecraft.player.getOffhandItem();
        if (!(stack.getItem() instanceof MapBookItem)) {
            stack = minecraft.player.getMainHandItem();
            if (!(stack.getItem() instanceof MapItem)) stack = minecraft.player.getOffhandItem();
            if (!(stack.getItem() instanceof MapItem)) {ci.cancel();return;}

            MapItemSavedData mapState = getSavedData(stack, minecraft.level);
            if (mapState!=null) {
                for (MapDecoration mapIcon : mapState.getDecorations()) {
                    if (!mapIcon.type().getRegisteredName().contains("player")) {
                        Vec3 c = minecraft.gameRenderer.getMainCamera().position();
                        float mapScale = (float) Math.pow(2, mapState.scale);
                        float offset = 64f * mapScale;
                        float x = (mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2);
                        float z = (mapState.centerZ - offset + (mapIcon.y() + 128 + 1) * mapScale / 2);

                        double a = getAngle(c, x, z, minecraft);
                        if (!(a <= -61.0) && !(a > 60.0)) {
                            int k = Mth.ceil((graphics.guiWidth() - 9) / 2.0F);
                            int m = (int) (a * 173.0 / 2.0 / 60.0);
                            double d = Math.sqrt((x-c.x)*(x-c.x)+(z-c.z)*(z-c.z));
                            if (d > 0.5 && d < 10000) {
                                int dd = (int) (255 * (1 - (d / 10000)));
                                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.parse(
                                                "hud/locator_bar_dot/map_decorations/" + mapIcon.getSpriteLocation().getPath()),
                                        k + m, top - 2, 9, 9, (new Color(255, 255, 255, dd)).hashCode());

                            }}
                    }
                }
            }
            ci.cancel();
            return;
        }


        for (MapStateData mapStateData : getMapStates(stack, minecraft.level)) {
            float render = 0.0f;
            if (minecraft.level.dimension().identifier().toString().contains(mapStateData.mapState.dimension.identifier().toString()))
                render = 1.0f;
            if (minecraft.level.dimension().identifier().toString().contains("the_nether") &&
                mapStateData.mapState.dimension.identifier().toString().contains("overworld"))
                render = 1 / 8.0f;
           if (render > 0) {
                for (MapDecoration mapIcon : mapStateData.mapState.getDecorations()) {
                    if (!mapIcon.type().getRegisteredName().contains("player")) {
                        Vec3 c = minecraft.gameRenderer.getMainCamera().position();
                        float mapScale = (float) Math.pow(2, mapStateData.mapState.scale);
                        float offset = 64f * mapScale;
                        float x = (mapStateData.mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2) * render;
                        float z = (mapStateData.mapState.centerZ - offset + (mapIcon.y() + 128 + 1) * mapScale / 2) * render;

                        double a = getAngle(c, x, z, minecraft);
                        if (!(a <= -61.0) && !(a > 60.0)) {
                            int k = Mth.ceil((graphics.guiWidth() - 9) / 2.0F);
                            int m = (int) (a * 173.0 / 2.0 / 60.0);
                            double d = Math.sqrt((x-c.x)*(x-c.x)+(z-c.z)*(z-c.z));
                            if (d > 0.5 && d < 10000) {
                                int dd = (int) (255 * (1 - (d / 10000)));
                                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.parse(
                                                "hud/locator_bar_dot/map_decorations/" + mapIcon.getSpriteLocation().getPath()),
                                        k + m, top - 2, 9, 9, (new Color(255, 255, 255, dd)).hashCode());
                            }
                        }
                    }
                }
            }
        }

        int id = -1;
        if (stack.has(DataComponents.MAP_ID)) {
            id = stack.get(DataComponents.MAP_ID).id();
        }
        Player thisPlayer = minecraft.player;
        MapBookState mps = MapBookStateManager.INSTANCE.getClientMapBookState(id);
        if (mps != null ) {
            if (mps.marker.dimension.contains(thisPlayer.level().dimension().identifier().toString())) {
                Vec3 c = minecraft.gameRenderer.getMainCamera().position();
                double x = mps.marker.x;
                double z = mps.marker.z;

                double a = getAngle(c, x, z, minecraft);
                if (!(a <= -61.0) && !(a > 60.0)) {
                    int k = Mth.ceil((graphics.guiWidth() - 9) / 2.0F);
                    int m = (int) (a * 173.0 / 2.0 / 60.0);
                    double d = Math.sqrt((x - c.x) * (x - c.x) + (z - c.z) * (z - c.z));
                    if (d > 0.5 && d < 10000) {
                        int dd = (int) (255 * (1 - (d / 10000)));
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.parse(
                                        "hud/locator_bar_dot/map_decorations/target_x"),
                                k + m, top - 2, 9, 9, (new Color(255, 255, 255, dd)).hashCode());
                    }
                }
            }


            MapBookPlayer p = new MapBookPlayer();
            p.setPlayer(thisPlayer);
            ArrayList<MapBookPlayer> mp = mps.players;
            if (mp != null) {
                try {
                    for (MapBookPlayer player : mp) {
                        if (player.dimension.contains(p.dimension)) {
                            if (!(player.name.contains(p.name) && p.name.contains(player.name))) {
                                Vec3 c = minecraft.gameRenderer.getMainCamera().position();

                                double x = player.x;
                                double y = player.y;
                                double z = player.z;

                                double dd = Math.sqrt((x-c.x)*(x-c.x)+(y-c.y)*(y-c.y)+(z-c.z)*(z-c.z));
                                double a = getAngle(c, x, z, minecraft);
                                if (!(a <= -61.0) && !(a > 60.0)) {
                                    int k = Mth.ceil((graphics.guiWidth() - 9) / 2.0F);
                                    int m = (int) (a * 173.0 / 2.0 / 60.0);

                                    int color = MapBookScreen.getColor(player, minecraft);
                                    WaypointStyle waypointStyle = minecraft.getWaypointStyles().get(WaypointStyleAssets.DEFAULT);
                                    Identifier identifier = waypointStyle.sprite((float) dd);
                                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier,
                                            k + m, top - 2, 9, 9, color);
                                    int n = aboveOrBelow(c, x, y, z, minecraft);

                                    if (n != 0) {
                                        byte o;
                                        Identifier identifier2;
                                        if (n == -1) {
                                            o = 6;
                                            identifier2 = LOCATOR_BAR_ARROW_DOWN;
                                        } else {
                                            o = -6;
                                            identifier2 = LOCATOR_BAR_ARROW_UP;
                                        }

                                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier2, k + m + 1, top + o, 7, 5);
                                    }
                                }
                            }
                        }
                    }
                } catch (ConcurrentModificationException ignored) {
                }
            }
        }

        ci.cancel();
    }

    @Unique
    private static double getAngle(Vec3 c, double x, double z, Minecraft client) {
        double a = -Math.atan((x - c.x) / (z - c.z));
        a *= 180 / Math.PI;
        if (z < c.z) a += 180;
        a -= client.gameRenderer.getMainCamera().yaw() % 360;
        a += 720;
        a += 180;
        a %= 360;
        a -= 180;
        return a;
    }

    @Unique
    private static int aboveOrBelow(Vec3 c, double x, double y, double z, Minecraft client) {
        double xz = Math.sqrt((x-c.x)*(x-c.x)+(z-c.z)*(z-c.z));
        double a = -Math.atan(xz / (y - c.y));
        a *= 180 / Math.PI;
        if (y < c.y) a += 180;
        a += client.gameRenderer.getMainCamera().xRot() % 360;
        a+=90;
        a += 720;
        a += 180;
        a %= 360;
        a -= 180;
        if (a<-60) return -1;
        if (a>60) return 1;
        return 0;
    }

    @Unique
    int getCenterY(Window window) {
        return window.getGuiScaledHeight() - 24 - 5;
    }

    @Unique
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

    @Unique
    private MapBookState getMapBookState(ItemStack stack) {
        int id = getMapBookId(stack) ;
        if (id == -1) return null;
        return MapBookStateManager.INSTANCE.getClientMapBookState(id);
    }

    @Unique
    private static int getMapBookId(ItemStack stack) {
        MapId mapIdComponent = stack.getOrDefault(DataComponents.MAP_ID, new MapId(-1));
        return mapIdComponent.id();
    }
}
