package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.LocatorBar;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.item.FilledMapItem.getMapState;

@Mixin(LocatorBar.class)
public class LocatorBarMixin {

    @Unique
    private static final Int2ObjectMap<Identifier> ARROWS = new Int2ObjectArrayMap<>(
            Map.of(1, Identifier.ofVanilla("hud/locator_bar_arrow_up"), -1, Identifier.ofVanilla("hud/locator_bar_arrow_down"))
    );

    @Inject(method = "renderAddons", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getWorld()Lnet/minecraft/world/World;"
    ))
    private void addBannerMarkers(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Local(ordinal = 1)int j){
        MinecraftClient client = MinecraftClient.getInstance();
        int i = getCenterY(client.getWindow());

        ItemStack stack = client.player.getMainHandStack();
        if (stack == null) return;
        if (!(stack.getItem() instanceof MapBookItem)) stack = client.player.getOffHandStack();
        if (!(stack.getItem() instanceof MapBookItem)) {
            stack = client.player.getMainHandStack();
            if (!(stack.getItem() instanceof FilledMapItem)) stack = client.player.getOffHandStack();
            if (!(stack.getItem() instanceof FilledMapItem)) return;

            MapState mapState = getMapState(stack, client.world);
            if (mapState!=null) {
                for (MapDecoration mapIcon : mapState.getDecorations()) {
                    if (!mapIcon.type().getIdAsString().contains("player")) {
                        Vec3d c = client.gameRenderer.getCamera().getPos();
                        float mapScale = (float) Math.pow(2, mapState.scale);
                        float offset = 64f * mapScale;
                        float x = (mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2);
                        float z = (mapState.centerZ - offset + (mapIcon.z() + 128 + 1) * mapScale / 2);

                        double a = getAngle(c, x, z, client);
                        if (!(a <= -61.0) && !(a > 60.0)) {
                            int k = MathHelper.ceil((context.getScaledWindowWidth() - 9) / 2.0F);
                            int m = (int) (a * 173.0 / 2.0 / 60.0);
                            double d = Math.sqrt((x-c.x)*(x-c.x)+(z-c.z)*(z-c.z));
                            if (d > 0.5 && d < 10000) {
                                int dd = (int) (255 * (1 - (d / 10000)));
                                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(
                                                "hud/locator_bar_dot/map_decorations/" + mapIcon.getAssetId().getPath()),
                                        k + m, i - 2, 9, 9, (new Color(255, 255, 255, dd)).hashCode());

                            }}
                    }
                }
            }
            return;
        }

        for (MapStateData mapStateData : getMapStates(stack, client.world)) {
            float render = 0.0f;
            if (client.world.getDimensionEntry().getIdAsString().contains(mapStateData.mapState.dimension.getValue().toString()))
                render = 1.0f;
            if (client.world.getDimensionEntry().getIdAsString().contains("the_nether") &&
                mapStateData.mapState.dimension.getValue().toString().contains("overworld"))
                render = 1 / 8.0f;
           if (render > 0) {
                for (MapDecoration mapIcon : mapStateData.mapState.getDecorations()) {
                    if (!mapIcon.type().getIdAsString().contains("player")) {
                        Vec3d c = client.gameRenderer.getCamera().getPos();
                        float mapScale = (float) Math.pow(2, mapStateData.mapState.scale);
                        float offset = 64f * mapScale;
                        float x = (mapStateData.mapState.centerX - offset + (mapIcon.x() + 128 + 1) * mapScale / 2) * render;
                        float z = (mapStateData.mapState.centerZ - offset + (mapIcon.z() + 128 + 1) * mapScale / 2) * render;

                        double a = getAngle(c, x, z, client);
                        if (!(a <= -61.0) && !(a > 60.0)) {
                            int k = MathHelper.ceil((context.getScaledWindowWidth() - 9) / 2.0F);
                            int m = (int) (a * 173.0 / 2.0 / 60.0);
                            double d = Math.sqrt((x-c.x)*(x-c.x)+(z-c.z)*(z-c.z));
                            if (d > 0.5 && d < 10000) {
                                int dd = (int) (255 * (1 - (d / 10000)));
                                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(
                                                "hud/locator_bar_dot/map_decorations/" + mapIcon.getAssetId().getPath()),
                                        k + m, i - 2, 9, 9, (new Color(255, 255, 255, dd)).hashCode());
                            }
                        }
                    }
                }
            }
        }

        int id = -1;
        if (stack.contains(DataComponentTypes.MAP_ID)) {
            id = stack.get(DataComponentTypes.MAP_ID).id();
        }
        PlayerEntity thisPlayer = client.player;
        MapBookState mps = MapBookStateManager.INSTANCE.getClientMapBookState(id);

        if (mps.marker.dimension.contains(thisPlayer.getWorld().getDimensionEntry().getIdAsString())) {
            Vec3d c = client.gameRenderer.getCamera().getPos();
            double x = mps.marker.x;
            double z = mps.marker.z;

            double a = getAngle(c, x, z, client);
            if (!(a <= -61.0) && !(a > 60.0)) {
                int k = MathHelper.ceil((context.getScaledWindowWidth() - 9) / 2.0F);
                int m = (int) (a * 173.0 / 2.0 / 60.0);
                double d = Math.sqrt((x-c.x)*(x-c.x)+(z-c.z)*(z-c.z));
                if (d > 0.5 && d < 10000) {
                    int dd = (int) (255 * (1 - (d / 10000)));
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(
                                    "hud/locator_bar_dot/map_decorations/target_x"),
                            k + m, i - 2, 9, 9, (new Color(255, 255, 255, dd)).hashCode());
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
                            Vec3d c = client.gameRenderer.getCamera().getPos();

                            double x = player.x;
                            double y = player.y;
                            double z = player.z;

                            double a = getAngle(c, x, z, client);
                            if (!(a <= -61.0) && !(a > 60.0)) {
                                int k = MathHelper.ceil((context.getScaledWindowWidth() - 9) / 2.0F);
                                int m = (int) (a * 173.0 / 2.0 / 60.0);

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

                                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("hud/locator_bar_dot/default_0"), k + m, i - 2, 9, 9, color);
                                int n = aboveOrBelow(c, x, y, z, client);
                                if (n != 0) {
                                    int o = n < 0 ? 9 : -5;
                                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ARROWS.get(n), 14, 5, j, 0, k + m + 1, i + o - 1, 7, 5);
                                }
                            }
                        }
                    }
                }
            } catch (ConcurrentModificationException ignored) {
            }
        }
    }

    @Unique
    private static double getAngle(Vec3d c, double x, double z, MinecraftClient client) {
        double a = -Math.atan((x - c.x) / (z - c.z));
        a *= 180 / Math.PI;
        if (z < c.z) a += 180;
        a -= client.gameRenderer.getCamera().getYaw() % 360;
        a += 720;
        a += 180;
        a %= 360;
        a -= 180;
        return a;
    }

    @Unique
    private static int aboveOrBelow(Vec3d c, double x, double y, double z, MinecraftClient client) {
        double xz = Math.sqrt((x-c.x)*(x-c.x)+(z-c.z)*(z-c.z));
        double a = -Math.atan(xz / (y - c.y));
        a *= 180 / Math.PI;
        if (y < c.y) a += 180;
        a -= client.gameRenderer.getCamera().getPitch() % 360;
        a += 720;
        a += 180;
        a %= 360;
        a -= 180;
        if (a>60)return -1;
        if (a<-60)return 1;
        return 0;
    }

    @Unique
    int getCenterY(Window window) {
        return window.getScaledHeight() - 24 - 5;
    }

    @Unique
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

    @Unique
    private MapBookState getMapBookState(ItemStack stack, World world) {
        int id = getMapBookId(stack) ;
        if (id == -1) return null;
        return MapBookStateManager.INSTANCE.getClientMapBookState(id);
    }

    @Unique
    private static int getMapBookId(ItemStack stack) {
        MapIdComponent mapIdComponent = stack.getOrDefault(DataComponentTypes.MAP_ID, null);
        if (mapIdComponent!=null) return mapIdComponent.id();
        return -1;
    }
}
