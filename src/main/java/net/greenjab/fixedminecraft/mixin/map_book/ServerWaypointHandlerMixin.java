package net.greenjab.fixedminecraft.mixin.map_book;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerWaypointHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWaypointHandler.class)
public class ServerWaypointHandlerMixin  {
    @Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
    private void dontAddPlayers(ServerPlayerEntity player, CallbackInfo ci) {
        ci.cancel();
    }
}
