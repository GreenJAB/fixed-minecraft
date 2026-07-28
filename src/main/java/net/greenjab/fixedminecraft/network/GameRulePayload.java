package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record GameRulePayload(GameRuleStatus rules) implements CustomPacketPayload {
    public static final Type<GameRulePayload> PACKET_ID = new Type<>(FixedMinecraft.id("gamerule_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, GameRulePayload> PACKET_CODEC = StreamCodec.composite(
            GameRuleNetwork.GAME_RULES,
            GameRulePayload::rules,
            GameRulePayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(PACKET_ID, PACKET_CODEC);
    }

}
