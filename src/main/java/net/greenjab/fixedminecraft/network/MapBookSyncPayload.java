package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

public record MapBookSyncPayload(int bookID, int[] mapIDs,  ArrayList<MapBookPlayer> players, MapBookPlayer marker) implements CustomPacketPayload {
    public static final Type<MapBookSyncPayload> PACKET_ID = new Type<>(FixedMinecraft.id("map_book_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MapBookSyncPayload> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            MapBookSyncPayload::bookID,
            IntArray.ARRAY_CODEC,
            MapBookSyncPayload::mapIDs,
            MapBookPlayerNetwork.ARRAY_CODEC,
            MapBookSyncPayload::players,
            MapBookPlayerNetwork.SINGLE,
            MapBookSyncPayload::marker,
            MapBookSyncPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(PACKET_ID, PACKET_CODEC);
    }

    @Nullable
    public static MapBookSyncPayload of(ServerPlayer player, ItemStack itemStack) {
        int bookId = MapBookItem.getMapBookId(itemStack);
        if (bookId == -1) return null;

        MapBookState mapBookState = MapBookStateManager.INSTANCE.getMapBookState(player.level().getServer(), bookId);
        if (mapBookState != null) {
            return new MapBookSyncPayload(bookId, mapBookState.mapIDs.stream().mapToInt(i -> i).toArray(), mapBookState.players, mapBookState.marker);
        }
        return null;
    }
}
