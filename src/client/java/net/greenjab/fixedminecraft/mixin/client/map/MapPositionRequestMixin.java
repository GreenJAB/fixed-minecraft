package net.greenjab.fixedminecraft.mixin.client.map;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.greenjab.fixedminecraft.network.MapPositionRequestPayload;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MapPositionRequestMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;putClientsideMapState(Lnet/minecraft/component/type/MapIdComponent;Lnet/minecraft/item/map/MapState;)V"), method = "onMapUpdate")
    private void requestMapPosition(MapUpdateS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworking.send(new MapPositionRequestPayload(packet.mapId()));
    }
}
