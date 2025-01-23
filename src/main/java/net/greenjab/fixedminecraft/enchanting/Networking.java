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
    public static Identifier BOOKSHELF_SYNC= Identifier.of("fixedminecraft", "update_block");

    public static void sendUpdatePacket(BlockPos pos) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);

            for(ServerPlayerEntity player: FixedMinecraft.INSTANCE.getSERVER().getPlayerManager().getPlayerList()) {
               // ServerPlayNetworking.send(player, BOOKSHELF_SYNC, buf);
                //TODO
            }
    }
}
