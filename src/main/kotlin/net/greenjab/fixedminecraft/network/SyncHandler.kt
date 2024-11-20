package net.greenjab.fixedminecraft.network

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.util.UUID
import kotlin.math.abs

object SyncHandler {
    val EXHAUSTION_SYNC: Identifier = Identifier("fixedminecraft", "exhaustion_sync")
    val SATURATION_SYNC: Identifier = Identifier("fixedminecraft", "saturation_sync")

    private fun makePacketBuf(value: Float): PacketByteBuf {
        val buf = PacketByteBuf(Unpooled.buffer())
        buf.writeFloat(value)
        return buf
    }

    /*
     * Sync saturation (vanilla MC only syncs when it hits 0)
     * Sync exhaustion (vanilla MC does not sync it at all)
     */
    private var lastSaturationLevels: HashMap<UUID, Float> = HashMap()
    private var lastExhaustionLevels: HashMap<UUID, Float> = HashMap()

    fun onPlayerUpdate(player: ServerPlayerEntity) {

        val lastSaturationLevel = lastSaturationLevels[player.uuid]
        val lastExhaustionLevel = lastExhaustionLevels[player.uuid]

        val saturation: Float = player.hungerManager.saturationLevel
        if (lastSaturationLevel == null || lastSaturationLevel != saturation) {
            ServerPlayNetworking.send(player, SATURATION_SYNC, makePacketBuf(saturation))
            lastSaturationLevels[player.uuid] = saturation
        }

        val exhaustionLevel: Float = player.hungerManager.exhaustion
        if (lastExhaustionLevel == null || abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
            ServerPlayNetworking.send(player, EXHAUSTION_SYNC, makePacketBuf(exhaustionLevel))
            lastExhaustionLevels[player.uuid] = exhaustionLevel
        }
    }

    fun onPlayerLoggedIn(player: ServerPlayerEntity) {
        lastSaturationLevels.remove(player.uuid)
        lastExhaustionLevels.remove(player.uuid)
    }

    private fun makeItemStackBuf(item: ItemStack): PacketByteBuf {
        val buf = PacketByteBuf(Unpooled.buffer())
        buf.writeItemStack(item)
        return buf
    }

    private fun makeMapBookSyncBuf(player: ServerPlayerEntity, id: Int): PacketByteBuf {
        val buf = PacketByteBuf(Unpooled.buffer())
        val mapBookState = MapBookStateManager.getMapBookState(player.server, id)

        if (mapBookState != null) {
            buf.writeVarInt(id)
            buf.writeIntArray(mapBookState.mapIDs.toIntArray())
            buf.writeVarInt(mapBookState.players.size)
            for (p in mapBookState.players) {
                p.toPacket(buf)
            }
        } else {
            buf.writeVarInt(-1)
        }

        return buf
    }

    val MAP_BOOK_OPEN: Identifier = Identifier("fixedminecraft", "map_book_open")
    val MAP_BOOK_SYNC: Identifier = Identifier("fixedminecraft", "map_book_sync")

    fun onOpenMapBook(player: ServerPlayerEntity, item: ItemStack) {
        ServerPlayNetworking.send(player, MAP_BOOK_OPEN, makeItemStackBuf(item))
    }

    fun mapBookSync(player: ServerPlayerEntity, id: Int) {
        ServerPlayNetworking.send(player, MAP_BOOK_SYNC, makeMapBookSyncBuf(player, id))
    }

}
