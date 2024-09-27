package net.greenjab.fixedminecraft.network

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.greenjab.fixedminecraft.map_book.MapBookScreen
import net.greenjab.fixedminecraft.enchanting.Networking.BOOKSHELF_SYNC
import net.greenjab.fixedminecraft.network.SyncHandler.EXHAUSTION_SYNC
import net.greenjab.fixedminecraft.network.SyncHandler.MAP_BOOK_OPEN
import net.greenjab.fixedminecraft.network.SyncHandler.MAP_BOOK_SYNC
import net.greenjab.fixedminecraft.network.SyncHandler.SATURATION_SYNC
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager
import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

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

        ClientPlayNetworking.registerGlobalReceiver(MAP_BOOK_OPEN) { client: MinecraftClient, handler: ClientPlayNetworkHandler?, buf: PacketByteBuf, responseSender: PacketSender? ->
            val itemStack = buf.readItemStack()

            client.execute {
                client.setScreen(MapBookScreen(itemStack))
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(MAP_BOOK_SYNC) { client: MinecraftClient, handler: ClientPlayNetworkHandler?, buf: PacketByteBuf, responseSender: PacketSender? ->
            val bookID = buf.readVarInt()
            if (bookID != -1) {
                val ids = buf.readIntArray()

                if (ids.isNotEmpty()) {
                    client.execute {
                        MapBookStateManager.putClientMapBookState(bookID, MapBookState(ids))
                    }
                }
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(BOOKSHELF_SYNC
        ) { client: MinecraftClient, handler: ClientPlayNetworkHandler?, buf: PacketByteBuf, responseSender: PacketSender? ->
            val pos = buf.readBlockPos()
            client.execute {
                assert(client.world != null)
                client.world!!.updateListeners(
                    pos,
                    client.world!!.getBlockState(pos),
                    client.world!!.getBlockState(pos),
                    Block.NOTIFY_LISTENERS
                )
            }
        }


    }
}
