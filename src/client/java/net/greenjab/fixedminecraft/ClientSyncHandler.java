package net.greenjab.fixedminecraft;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.greenjab.fixedminecraft.screens.MapBookScreen;
import net.greenjab.fixedminecraft.network.MapBookOpenPayload;
import net.greenjab.fixedminecraft.network.MapBookSyncPayload;
import net.greenjab.fixedminecraft.network.MapPositionPayload;
import net.greenjab.fixedminecraft.network.SaturationSyncPayload;
import net.greenjab.fixedminecraft.network.TrainPayload;
import net.greenjab.fixedminecraft.network.VillagerNeedsPayload;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateAccessor;
import net.greenjab.fixedminecraft.registry.other.FixedFurnaceMinecartEntity;
import net.greenjab.fixedminecraft.registry.registries.ParticleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

/** Credit: Nettakrim, Squeek502, Bawnorton */
public class ClientSyncHandler {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SaturationSyncPayload.ID, (payload, context) ->
                context.client().execute(() ->
                        context.client().player.getFoodData().setSaturation(payload.getSaturation())));

        ClientPlayNetworking.registerGlobalReceiver(MapBookOpenPayload.PACKET_ID, ClientSyncHandler::mapBookOpen);
        ClientPlayNetworking.registerGlobalReceiver(MapBookSyncPayload.PACKET_ID, ClientSyncHandler::mapBookSync);
        ClientPlayNetworking.registerGlobalReceiver(MapPositionPayload.PACKET_ID, ClientSyncHandler::mapPosition);
        ClientPlayNetworking.registerGlobalReceiver(TrainPayload.PACKET_ID, ClientSyncHandler::train);
        ClientPlayNetworking.registerGlobalReceiver(VillagerNeedsPayload.PACKET_ID, ClientSyncHandler::villagerNeed);

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
            ClientLevel level = context.client().level;
            if (level != null) {
                MapItemSavedData mapstate = level.getMapData(payload.mapIdComponent());
                if (mapstate != null) {
                    ((MapStateAccessor)mapstate).fixedminecraft$setPosition(payload.centerX(), payload.centerZ());
                }
            }
        });
    }

    private static void train(TrainPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()-> {
            ClientLevel level = context.client().level;
            if (level != null) {
                Entity entity = level.getEntity((payload.train().getFirst()));
                if (entity instanceof FixedFurnaceMinecartEntity furnaceMinecart) {
                    furnaceMinecart.setTrain(payload.train());
                }
            }
        });
    }

    private static void villagerNeed(VillagerNeedsPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(()-> {
            ClientLevel level = context.client().level;
            if (level != null) {
                Entity entity = level.getEntity((payload.villager()));
                if (entity instanceof Villager villager) {
                    RandomSource random = villager.getRandom();
                    Minecraft.getInstance().player.sendSystemMessage(Component.translatable("entity.fixedminecraft.villager."+payload.need(), villager.getName()));

                    SimpleParticleType particle = switch (payload.need()) {
                        case "hungry","very_hungry" -> ParticleRegistry.VILLAGER_HUNGRY;
                        case "tired","very_tired" -> ParticleRegistry.VILLAGER_TIRED;
                        case "lonely","very_lonely" -> ParticleRegistry.VILLAGER_LONELY;
                        case "lazy","very_lazy" -> ParticleRegistry.VILLAGER_LAZY;
                        case "dark","very_dark" -> ParticleRegistry.VILLAGER_DARK;
                        case "night" -> ParticleRegistry.VILLAGER_NIGHT;
                        default -> null;
                    };
                    if (particle != null) {
                        Vec3 d = Minecraft.getInstance().player.position().subtract(villager.position()).horizontal().normalize();
                        level.addAlwaysVisibleParticle(
                                particle,
                                true,
                                villager.getX() + d.x*0.5 + random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1),
                                villager.getY() + 1 + random.nextDouble() / 2.0,
                                villager.getZ() + d.z*0.5 + random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1),
                                0.0,
                                0.02,
                                0.0
                        );
                    }
                }
            }
        });
    }
}
