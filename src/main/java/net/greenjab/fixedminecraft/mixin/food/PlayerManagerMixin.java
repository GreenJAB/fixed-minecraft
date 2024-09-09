package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.greenjab.fixedminecraft.network.SyncHandler;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
    @Inject(at = @At("TAIL"), method = "onPlayerConnect")
    private void onPlayerConnect(ClientConnection conn, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo info)
    {
        SyncHandler.INSTANCE.onPlayerLoggedIn(player);
    }
}
