package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record HorseDismountPayload(UUID horse) implements CustomPacketPayload {
    public static final Type<HorseDismountPayload> PACKET_ID = new Type<>(FixedMinecraft.id("horse_dismount"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HorseDismountPayload> PACKET_CODEC = StreamCodec.composite(
            UUIDNetwork.SINGLE_CODEC,
            HorseDismountPayload::horse,
            HorseDismountPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(PACKET_ID, PACKET_CODEC);
    }
}
