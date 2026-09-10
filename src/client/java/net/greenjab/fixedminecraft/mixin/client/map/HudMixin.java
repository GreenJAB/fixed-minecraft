package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Hud.class)
public abstract class HudMixin {
     @ModifyExpressionValue(method = "nextContextualInfoState", at = @At(
             value = "INVOKE",
             target = "Lnet/minecraft/client/waypoints/ClientWaypointManager;hasWaypoints()Z"
     ))
     private boolean renderMapWayPoints(boolean original) {
         LocalPlayer player = Minecraft.getInstance().player;
         Minecraft client = Minecraft.getInstance();
         AtomicBoolean hasWaypoint = new AtomicBoolean(false);
         assert client.player != null;
         assert client.getCameraEntity() != null;
         client.player.connection.getWaypointManager().forEachWaypoint(client.getCameraEntity(), (waypoint) -> {
             if (!(Boolean)waypoint.id().left().map((uuid) -> uuid.equals(client.getCameraEntity().getUUID())).orElse(false)) {
                 hasWaypoint.set(hasWaypoint.get() || (waypoint.icon().style != WaypointStyleAssets.DEFAULT));
             }
         });

         return hasWaypoint.get() ||
                player.getMainHandItem().getItem().components().has(DataComponents.MAP_ID) ||
                player.getOffhandItem().getItem().components().has(DataComponents.MAP_ID);
     }
}
