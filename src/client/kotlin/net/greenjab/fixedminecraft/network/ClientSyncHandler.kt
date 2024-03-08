package net.greenjab.fixedminecraft.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientSyncHandler {
    @Environment(EnvType.CLIENT)
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SyncHandler.INSTANCE.getEXHAUSTION_SYNC(), (client, handler, buf, responseSender) -> {
            float exhaustion = buf.readFloat();
            client.execute(() -> {
                client.player.getHungerManager().setExhaustion(exhaustion);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncHandler.INSTANCE.getSATURATION_SYNC(), (client, handler, buf, responseSender) -> {
            float saturation = buf.readFloat();
            client.execute(() -> {
                client.player.getHungerManager().setSaturationLevel(saturation);
            });
        });
    }
}
