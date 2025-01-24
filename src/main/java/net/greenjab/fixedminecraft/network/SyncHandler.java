package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.util.ExhaustionHelper;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncHandler
{
    public static void init()
    {
        PayloadTypeRegistry.playS2C().register(ExhaustionSyncPayload.ID, ExhaustionSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SaturationSyncPayload.ID, SaturationSyncPayload.CODEC);
        MapBookOpenPayload.register();
        MapBookSyncPayload.register();
        MapPositionPayload.register();
        MapPositionRequestPayload.register();
        //BookShelfSyncPayload.register();
    }

    /*
     * Sync saturation (vanilla MC only syncs when it hits 0)
     * Sync exhaustion (vanilla MC does not sync it at all)
     */
    private static final Map<UUID, Float> lastSaturationLevels = new HashMap<UUID, Float>();
    private static final Map<UUID, Float> lastExhaustionLevels = new HashMap<UUID, Float>();

    public static void onPlayerUpdate(ServerPlayerEntity player)
    {
        Float lastSaturationLevel = lastSaturationLevels.get(player.getUuid());
        Float lastExhaustionLevel = lastExhaustionLevels.get(player.getUuid());

        float saturation = player.getHungerManager().getSaturationLevel();
        if (lastSaturationLevel == null || lastSaturationLevel != saturation)
        {
            ServerPlayNetworking.send(player, new SaturationSyncPayload(saturation));
            lastSaturationLevels.put(player.getUuid(), saturation);
        }

        float exhaustionLevel = ExhaustionHelper.getExhaustion(player);
        if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f)
        {
            ServerPlayNetworking.send(player, new ExhaustionSyncPayload(exhaustionLevel));
            lastExhaustionLevels.put(player.getUuid(), exhaustionLevel);
        }
    }
    public static void onPlayerLoggedIn(ServerPlayerEntity player)
    {
        lastSaturationLevels.remove(player.getUuid());
        lastExhaustionLevels.remove(player.getUuid());
    }
}
