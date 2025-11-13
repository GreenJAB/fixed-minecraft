package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncHandler
{
    public static void init()
    {
        PayloadTypeRegistry.playS2C().register(SaturationSyncPayload.ID, SaturationSyncPayload.CODEC);
        MapBookOpenPayload.register();
        MapBookSyncPayload.register();
        MapPositionPayload.register();
        MapPositionRequestPayload.register();
    }

    private static final Map<UUID, Float> lastSaturationLevels = new HashMap<>();

    public static void onPlayerUpdate(ServerPlayerEntity player)
    {
        Float lastSaturationLevel = lastSaturationLevels.get(player.getUuid());

        float saturation = player.getHungerManager().getSaturationLevel();
        if (lastSaturationLevel == null || lastSaturationLevel != saturation)
        {
            ServerPlayNetworking.send(player, new SaturationSyncPayload(saturation));
            lastSaturationLevels.put(player.getUuid(), saturation);
        }
    }
    public static void onPlayerLoggedIn(ServerPlayerEntity player)
    {
        lastSaturationLevels.remove(player.getUuid());
    }
}
