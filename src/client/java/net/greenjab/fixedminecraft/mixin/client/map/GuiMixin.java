package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Gui.class)
public abstract class GuiMixin {
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
                player.getMainHandItem().getItem() instanceof MapBookItem ||
                player.getOffhandItem().getItem() instanceof MapBookItem ||
                player.getMainHandItem().getItem() instanceof MapItem ||
                player.getOffhandItem().getItem() instanceof MapItem;
     }
}
