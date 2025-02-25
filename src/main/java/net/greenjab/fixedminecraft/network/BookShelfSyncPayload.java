package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public record BookShelfSyncPayload(BlockPos pos) implements CustomPayload {
    public static final Id<BookShelfSyncPayload> PACKET_ID = new Id<>(Identifier.of("fixedminecraft", "book_shelf_sync"));

    public static final PacketCodec<RegistryByteBuf, BookShelfSyncPayload> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC,
            BookShelfSyncPayload::pos,
            BookShelfSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(PACKET_ID, PACKET_CODEC);
    }

    public static @NotNull BookShelfSyncPayload of(BlockPos pos) {
        return new BookShelfSyncPayload(pos);
    }

}
