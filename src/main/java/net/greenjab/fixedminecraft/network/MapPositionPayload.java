package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.jspecify.annotations.NonNull;

public record MapPositionPayload(MapId mapIdComponent, int centerX, int centerZ) implements CustomPacketPayload {
    public static final Type<MapPositionPayload> PACKET_ID = new Type<>(FixedMinecraft.id("map_position"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MapPositionPayload> PACKET_CODEC = StreamCodec.composite(
            MapId.STREAM_CODEC,
            MapPositionPayload::mapIdComponent,
            ByteBufCodecs.VAR_INT,
            MapPositionPayload::centerX,
            ByteBufCodecs.VAR_INT,
            MapPositionPayload::centerZ,
            MapPositionPayload::new
    );


    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(PACKET_ID, PACKET_CODEC);
    }
}
