package net.greenjab.fixedminecraft.mixin.map_book;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWaypointManager.class)
public abstract class ServerWaypointManagerMixin {
    @Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
    private void dontAddPlayers(ServerPlayer player, CallbackInfo ci) {
        if (FixedMinecraft.gameRules.global_locator_bar) return;
        ci.cancel();
    }
}
