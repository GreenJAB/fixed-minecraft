package net.greenjab.fixedminecraft.enchanting;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.network.BookShelfSyncPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class Networking {
    public static final Object SERVER_LOCK = new Object();

    public static void sendUpdatePacket(BlockPos pos) {
            for(ServerPlayerEntity player: Objects.requireNonNull(FixedMinecraft.INSTANCE.getSERVER()).getPlayerManager().getPlayerList()) {
                BookShelfSyncPayload payload = BookShelfSyncPayload.of(pos);
                ServerPlayNetworking.send(player, payload);
            }
    }
}
