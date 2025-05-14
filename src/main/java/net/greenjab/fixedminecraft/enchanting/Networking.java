package net.greenjab.fixedminecraft.enchanting;


public class Networking {
    public static final Object SERVER_LOCK = new Object();

    /*public static void sendUpdatePacket(BlockPos pos) {
            for(ServerPlayerEntity player: Objects.requireNonNull(FixedMinecraft.SERVER).getPlayerManager().getPlayerList()) {
                BookShelfSyncPayload payload = BookShelfSyncPayload.of(pos);
                ServerPlayNetworking.send(player, payload);
            }
    }*/
}
