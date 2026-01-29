package net.greenjab.fixedminecraft;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.greenjab.fixedminecraft.map_book.MapBookScreen;
import net.greenjab.fixedminecraft.network.MapBookOpenPayload;
import net.greenjab.fixedminecraft.network.MapBookSyncPayload;
import net.greenjab.fixedminecraft.network.MapPositionPayload;
import net.greenjab.fixedminecraft.network.SaturationSyncPayload;
import net.greenjab.fixedminecraft.network.TrainPayload;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateAccessor;
import net.greenjab.fixedminecraft.registry.other.FixedFurnaceMinecartEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.map.MapState;

import java.util.ArrayList;

/** Credit: Nettakrim, Squeek502, Bawnorton */
public class ClientSyncHandler {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SaturationSyncPayload.ID, (payload, context) ->
                context.client().execute(() ->
                        context.client().player.getHungerManager().setSaturationLevel(payload.getSaturation())));

        ClientPlayNetworking.registerGlobalReceiver(MapBookOpenPayload.PACKET_ID, ClientSyncHandler::mapBookOpen);
        ClientPlayNetworking.registerGlobalReceiver(MapBookSyncPayload.PACKET_ID, ClientSyncHandler::mapBookSync);
        ClientPlayNetworking.registerGlobalReceiver(MapPositionPayload.PACKET_ID, ClientSyncHandler::mapPosition);
        ClientPlayNetworking.registerGlobalReceiver(TrainPayload.PACKET_ID, ClientSyncHandler::train);

    }
    private static void mapBookOpen(MapBookOpenPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> context.client().setScreen(new MapBookScreen(payload.itemStack())));
    }

    private static void mapBookSync(MapBookSyncPayload payload, ClientPlayNetworking.Context context) {
        if (payload.mapIDs().length > 0) {
            context.client().execute(() -> {
                ArrayList<Integer> ints = new ArrayList<>();
                for (int i = 0; i < payload.mapIDs().length;i++) {
                    ints.add(payload.mapIDs()[i]);
                }
                MapBookStateManager.INSTANCE.putClientMapBookState(
                        payload.bookID(),

                        new MapBookState(ints, payload.players(), payload.marker())
                );
            });
        }
    }

    private static void mapPosition(MapPositionPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()-> {
            ClientWorld world = context.client().world;
            if (world != null) {
                MapState mapstate = world.getMapState(payload.mapIdComponent());
                if (mapstate != null) {
                    ((MapStateAccessor)mapstate).fixedminecraft$setPosition(payload.centerX(), payload.centerZ());
                }
            }
        });
    }

    private static void train(TrainPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()-> {
            ClientWorld world = context.client().world;
            if (world != null) {
                Entity entity = world.getEntity((payload.train().get(0)));
                if (entity instanceof FixedFurnaceMinecartEntity furnaceMinecart) {
                    furnaceMinecart.setTrain(payload.train());
                }
            }
        });
    }
}
