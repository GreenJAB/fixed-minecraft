package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public record MapBookOpenPayload(ItemStack itemStack) implements CustomPacketPayload {
    public static final Type<MapBookOpenPayload> PACKET_ID = new Type<>(FixedMinecraft.id("map_book_open"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MapBookOpenPayload> PACKET_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            MapBookOpenPayload::itemStack,
            MapBookOpenPayload::new
    );


    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(PACKET_ID, PACKET_CODEC);
    }
}
