package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.hud.HUDOverlayHandler;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public class InGameHudMixin {
     @ModifyExpressionValue(method = "getCurrentBarType", at = @At(
             value = "INVOKE",
             target = "Lnet/minecraft/client/world/ClientWaypointHandler;hasWaypoint()Z"
     ))
     private boolean renderFoodPost(boolean original) {
         MinecraftClient client = MinecraftClient.getInstance();
         ItemStack stack = client.player.getMainHandStack();
         if (stack != null) {
             if (!(stack.getItem() instanceof MapBookItem)) stack = client.player.getOffHandStack();
         }
         return original || stack.getItem() instanceof MapBookItem;
     }
}
