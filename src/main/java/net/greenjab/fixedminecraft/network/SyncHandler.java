package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncHandler
{
    public static void init()
    {
        PayloadTypeRegistry.clientboundPlay().register(SaturationSyncPayload.ID, SaturationSyncPayload.CODEC);
        MapBookOpenPayload.register();
        MapBookSyncPayload.register();
        MapPositionPayload.register();
        MapPositionRequestPayload.register();
        TrainPayload.register();
        VillagerNeedsPayload.register();
    }

    private static final Map<UUID, Float> lastSaturationLevels = new HashMap<>();

    public static void onPlayerUpdate(ServerPlayer player)
    {
        Float lastSaturationLevel = lastSaturationLevels.get(player.getUUID());

        float saturation = player.getFoodData().getSaturationLevel();
        if (lastSaturationLevel == null || lastSaturationLevel != saturation)
        {
            ServerPlayNetworking.send(player, new SaturationSyncPayload(saturation));
            lastSaturationLevels.put(player.getUUID(), saturation);
        }
    }
    public static void onPlayerLoggedIn(ServerPlayer player)
    {
        lastSaturationLevels.remove(player.getUUID());
    }
}
