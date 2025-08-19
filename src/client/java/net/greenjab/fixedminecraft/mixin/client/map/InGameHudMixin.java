package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.world.waypoint.WaypointStyles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(InGameHud.class)
public class InGameHudMixin {
     @ModifyExpressionValue(method = "getCurrentBarType", at = @At(
             value = "INVOKE",
             target = "Lnet/minecraft/client/world/ClientWaypointHandler;hasWaypoint()Z"
     ))
     private boolean renderFoodPost(boolean original) {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         MinecraftClient client = MinecraftClient.getInstance();
         AtomicBoolean hasWaypoint = new AtomicBoolean(false);
         client.player.networkHandler.getWaypointHandler().forEachWaypoint(client.getCameraEntity(), (waypoint) -> {
             if (!(Boolean)waypoint.getSource().left().map((uuid) -> {
                 return uuid.equals(client.getCameraEntity().getUuid());
             }).orElse(false)) {
                 hasWaypoint.set(hasWaypoint.get() || (waypoint.getConfig().style != WaypointStyles.DEFAULT));
             }
         });

         return hasWaypoint.get() ||
                player.getMainHandStack().getItem() instanceof MapBookItem ||
                player.getOffHandStack().getItem() instanceof MapBookItem ||
                player.getMainHandStack().getItem() instanceof FilledMapItem ||
                player.getOffHandStack().getItem() instanceof FilledMapItem;
     }
}
