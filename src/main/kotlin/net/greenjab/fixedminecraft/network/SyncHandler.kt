package net.greenjab.fixedminecraft.network

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.util.UUID

object SyncHandler {
    val EXHAUSTION_SYNC: Identifier = Identifier("fixedminecraft", "exhaustion_sync")
    val SATURATION_SYNC: Identifier = Identifier("fixedminecraft", "saturation_sync")

    fun makePacketBuf(value: Float): PacketByteBuf {
        var buf = PacketByteBuf(Unpooled.buffer())
        buf.writeFloat(value)
        return buf
    }

    /*
     * Sync saturation (vanilla MC only syncs when it hits 0)
     * Sync exhaustion (vanilla MC does not sync it at all)
     */
    var lastSaturationLevels: HashMap<UUID, Float> = HashMap<UUID, Float>()
    var lastExhaustionLevels: HashMap<UUID, Float> = HashMap<UUID, Float>()

    fun onPlayerUpdate(player: ServerPlayerEntity) {

        var lastSaturationLevel = lastSaturationLevels.get(player.uuid)
        var lastExhaustionLevel = lastExhaustionLevels.get(player.uuid)

        var saturation: Float = player.hungerManager.saturationLevel
        if (lastSaturationLevel == null || lastSaturationLevel != saturation) {
            ServerPlayNetworking.send(player, SATURATION_SYNC, makePacketBuf(saturation))
            lastSaturationLevels.put(player.uuid, saturation)
        }

        var exhaustionLevel: Float = player.hungerManager.exhaustion
        if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
            ServerPlayNetworking.send(player, EXHAUSTION_SYNC, makePacketBuf(exhaustionLevel))
            lastExhaustionLevels.put(player.uuid, exhaustionLevel)
        }
    }

    fun onPlayerLoggedIn(player: ServerPlayerEntity) {
        lastSaturationLevels.remove(player.uuid)
        lastExhaustionLevels.remove(player.uuid)
    }

    fun makeItemStackBuf(item: ItemStack): PacketByteBuf {
        val buf = PacketByteBuf(Unpooled.buffer())
        buf.writeItemStack(item)
        return buf
    }

    fun makeMapBookSyncBuf(player: ServerPlayerEntity, item: ItemStack): PacketByteBuf {
        val buf = PacketByteBuf(Unpooled.buffer())

        val id = (item.item as MapBookItem).getMapBookId(item)
        val mapBookState = MapBookStateManager.getMapBookState(player.server, id)

        if (mapBookState != null) {
            buf.writeVarInt(id!!)
            buf.writeIntArray(mapBookState.mapIDs.toIntArray())
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

    fun mapBookSync(player: ServerPlayerEntity, item: ItemStack) {
        ServerPlayNetworking.send(player, MAP_BOOK_SYNC, makeMapBookSyncBuf(player, item))
    }
}
