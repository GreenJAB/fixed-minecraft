package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;
import java.util.UUID;

public record VillagerNeedsPayload(UUID villager, String need) implements CustomPacketPayload {
    public static final Type<VillagerNeedsPayload> PACKET_ID = new Type<>(FixedMinecraft.id("villager_need"));

    public static final StreamCodec<RegistryFriendlyByteBuf, VillagerNeedsPayload> PACKET_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            VillagerNeedsPayload::villager,
            ByteBufCodecs.STRING_UTF8,
            VillagerNeedsPayload::need,
            VillagerNeedsPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(PACKET_ID, PACKET_CODEC);
    }
}
