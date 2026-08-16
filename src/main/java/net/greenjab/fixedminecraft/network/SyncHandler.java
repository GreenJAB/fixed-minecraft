package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

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
        HorseDismountPayload.register();
        VillagerNeedsPayload.register();
        GameRulePayload.register();

        ServerPlayNetworking.registerGlobalReceiver(HorseDismountPayload.PACKET_ID, SyncHandler::horse_dimount);
    }

    private static void horse_dimount(HorseDismountPayload payload, ServerPlayNetworking.Context context) {
        context.server().execute(()-> {
            Entity entity = context.player().level().getEntity(payload.horse());
            if (entity instanceof AbstractHorse horse) {
                horse.stopRiding();
            }
        });
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
