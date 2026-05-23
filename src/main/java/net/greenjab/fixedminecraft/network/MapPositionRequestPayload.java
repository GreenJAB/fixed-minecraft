package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jspecify.annotations.NonNull;

public record MapPositionRequestPayload(MapId mapIdComponent) implements CustomPacketPayload {
    public static final Type<MapPositionRequestPayload> PACKET_ID = new Type<>(FixedMinecraft.id("map_position_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MapPositionRequestPayload> PACKET_CODEC = StreamCodec.composite(
            MapId.STREAM_CODEC,
            MapPositionRequestPayload::mapIdComponent,
            MapPositionRequestPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(PACKET_ID, PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, MapPositionRequestPayload::onRequest);
    }

    private static void onRequest(MapPositionRequestPayload payload, ServerPlayNetworking.Context context) {
        MapItemSavedData mapState = context.player().level().getMapData(payload.mapIdComponent);
        if (mapState == null) return;

        ServerPlayNetworking.send(context.player(), new MapPositionPayload(payload.mapIdComponent, mapState.centerX, mapState.centerZ));
    }
}
