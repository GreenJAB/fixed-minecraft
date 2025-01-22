package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MapBookOpenPayload(ItemStack itemStack) implements CustomPayload {
    public static final Id<MapBookOpenPayload> PACKET_ID = new Id<>(Identifier.of("fixedminecraft", "map_book_open"));

    public static final PacketCodec<RegistryByteBuf, MapBookOpenPayload> PACKET_CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC,
            MapBookOpenPayload::itemStack,
            MapBookOpenPayload::new
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(PACKET_ID, PACKET_CODEC);
    }
}
