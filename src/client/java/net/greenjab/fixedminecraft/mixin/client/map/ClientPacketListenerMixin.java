package net.greenjab.fixedminecraft.mixin.client.map;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.greenjab.fixedminecraft.network.MapPositionRequestPayload;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Nettakrim */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;overrideMapData(Lnet/minecraft/world/level/saveddata/maps/MapId;Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;)V"), method = "handleMapItemData")
    private void requestMapPosition(ClientboundMapItemDataPacket packet, CallbackInfo ci) {
        ClientPlayNetworking.send(new MapPositionRequestPayload(packet.mapId()));
    }
}
