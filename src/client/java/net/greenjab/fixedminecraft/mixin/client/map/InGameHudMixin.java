package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.FilledMapItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public class InGameHudMixin {
     @ModifyExpressionValue(method = "getCurrentBarType", at = @At(
             value = "INVOKE",
             target = "Lnet/minecraft/client/world/ClientWaypointHandler;hasWaypoint()Z"
     ))
     private boolean renderFoodPost(boolean original) {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         return original ||
                player.getMainHandStack().getItem() instanceof MapBookItem ||
                player.getOffHandStack().getItem() instanceof MapBookItem ||
                player.getMainHandStack().getItem() instanceof FilledMapItem ||
                player.getOffHandStack().getItem() instanceof FilledMapItem;
     }
}
