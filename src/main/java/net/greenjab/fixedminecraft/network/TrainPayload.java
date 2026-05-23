package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.UUID;

public record TrainPayload(ArrayList<UUID> train) implements CustomPacketPayload {
    public static final Type<TrainPayload> PACKET_ID = new Type<>(FixedMinecraft.id("train"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TrainPayload> PACKET_CODEC = StreamCodec.composite(
            TrainNetwork.ARRAY_CODEC,
            TrainPayload::train,
            TrainPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(PACKET_ID, PACKET_CODEC);
    }
}
