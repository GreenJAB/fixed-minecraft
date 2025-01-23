package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MapPositionPayload(MapIdComponent mapIdComponent, int centerX, int centerZ) implements CustomPayload {
    public static final Id<MapPositionPayload> PACKET_ID = new Id<>(Identifier.of("fixedminecraft", "map_position"));

    public static final PacketCodec<RegistryByteBuf, MapPositionPayload> PACKET_CODEC = PacketCodec.tuple(
            MapIdComponent.PACKET_CODEC,
            MapPositionPayload::mapIdComponent,
            PacketCodecs.VAR_INT,
            MapPositionPayload::centerX,
            PacketCodecs.VAR_INT,
            MapPositionPayload::centerZ,
            MapPositionPayload::new
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(PACKET_ID, PACKET_CODEC);
    }
}
