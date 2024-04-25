package net.greenjab.fixedminecraft.network

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.greenjab.fixedminecraft.items.map_book.MapBookItem
import net.greenjab.fixedminecraft.items.map_book.MapBookState
import net.greenjab.fixedminecraft.items.map_book.MapBookStateManager
import net.greenjab.fixedminecraft.map_book.MapBookScreen
import net.greenjab.fixedminecraft.network.SyncHandler.EXHAUSTION_SYNC
import net.greenjab.fixedminecraft.network.SyncHandler.OPEN_MAP_BOOK
import net.greenjab.fixedminecraft.network.SyncHandler.SATURATION_SYNC
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

object ClientSyncHandler {
    @Environment(EnvType.CLIENT)
    fun init() {
        ClientPlayNetworking.registerGlobalReceiver(EXHAUSTION_SYNC) { client: MinecraftClient, handler: ClientPlayNetworkHandler?, buf: PacketByteBuf, responseSender: PacketSender? ->
            val exhaustion = buf.readFloat()
            client.execute {
                client.player?.hungerManager?.exhaustion = exhaustion
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(SATURATION_SYNC) { client: MinecraftClient, handler: ClientPlayNetworkHandler?, buf: PacketByteBuf, responseSender: PacketSender? ->
            val saturation = buf.readFloat()
            client.execute {
                client.player?.hungerManager?.saturationLevel = saturation
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(OPEN_MAP_BOOK) { client: MinecraftClient, handler: ClientPlayNetworkHandler?, buf: PacketByteBuf, responseSender: PacketSender? ->
            val itemStack = buf.readItemStack()
            val ids = buf.readIntArray()

            if (ids.isNotEmpty()) {
                client.execute {
                    MapBookStateManager.putClientMapBookState(
                        (itemStack.item as MapBookItem).getMapBookId(itemStack),
                        MapBookState(ids)
                    )
                }
            }

            client.execute {
                client.setScreen(MapBookScreen(itemStack))
            }
        }
    }
}
