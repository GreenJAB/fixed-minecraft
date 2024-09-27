package net.greenjab.fixedminecraft.enchanting;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class Networking {
    public static final Object SERVER_LOCK = new Object();
    private static boolean initialized = false;
    public static Identifier BOOKSHELF_SYNC= new Identifier("fixedminecraft", "update_block");

    public static void sendUpdatePacket(BlockPos pos) {
        //waitForServer(() -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);

            for(ServerPlayerEntity player: FixedMinecraft.INSTANCE.getSERVER().getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(player, BOOKSHELF_SYNC, buf);
            }
        //});
    }

    /*private static void waitForServer(Runnable runnable) {
        if(!initialized) {
            new Thread(() -> {
                synchronized (SERVER_LOCK) {
                    while (BetterBookshelves.SERVER == null) {
                        try {
                            SERVER_LOCK.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                initialized = true;
                runnable.run();
            }).start();
        } else {
            runnable.run();
        }
    }*/
}
