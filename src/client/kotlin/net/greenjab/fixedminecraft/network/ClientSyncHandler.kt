package net.greenjab.fixedminecraft.network

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.greenjab.fixedminecraft.map_book.MapBookScreen
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager
import net.greenjab.fixedminecraft.util.ExhaustionHelper
import net.minecraft.entity.player.PlayerEntity


object ClientSyncHandler {
    @Environment(EnvType.CLIENT)
    fun init() {
        ClientPlayNetworking.registerGlobalReceiver(
            ExhaustionSyncPayload.ID
        ) { payload: ExhaustionSyncPayload, context: ClientPlayNetworking.Context ->
            context.client().execute {
                ExhaustionHelper.setExhaustion(context.client().player as PlayerEntity, payload.exhaustion)
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(
            SaturationSyncPayload.ID
        ) { payload: SaturationSyncPayload, context: ClientPlayNetworking.Context ->
            context.client().execute {
                context.client().player!!.hungerManager.saturationLevel = payload.saturation
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(MapBookOpenPayload.PACKET_ID, ClientSyncHandler::mapBookOpen);
        ClientPlayNetworking.registerGlobalReceiver(MapBookSyncPayload.PACKET_ID, ClientSyncHandler::mapBookSync);
        //ClientPlayNetworking.registerGlobalReceiver(MapPositionPayload.PACKET_ID, ClientSyncHandler::mapPosition);

//TODO
        //ClientPlayNetworking.registerGlobalReceiver(BookShelfSyncPayload.PACKET_ID, ClientSyncHandler::bookShelfSync);

        /*ClientPlayNetworking.registerGlobalReceiver(BOOKSHELF_SYNC
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

        ClientPlayNetworking.registerGlobalReceiver(
            BookShelfSyncPayload.ID
        ) { payload: BookShelfSyncPayload, context: ClientPlayNetworking.Context ->
            context.client().execute {
                context.client().world!!.updateListeners(
                    pos,
                    context.client().world!!.getBlockState(pos),
                    context.client().world!!.getBlockState(pos),
                    Block.NOTIFY_LISTENERS
                )
            }
        }*/

        /*ClientPlayNetworking.registerGlobalReceiver(BOOKSHELF_SYNC
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
        }*/

    }
    private fun mapBookOpen(payload: MapBookOpenPayload, context: ClientPlayNetworking.Context) {
        context.client().execute { context.client().setScreen(MapBookScreen(payload.itemStack)) }
    }

    private fun mapBookSync(payload: MapBookSyncPayload, context: ClientPlayNetworking.Context) {
        if (payload.mapIDs.size > 0) {
            context.client().execute {
                MapBookStateManager.INSTANCE.putClientMapBookState(
                    payload.bookID,
                    MapBookState(payload.mapIDs)
                )
            }
        }
    }

    /*private fun mapPosition(payload: MapPositionPayload, context: ClientPlayNetworking.Context) {
        context.client().execute {
            val world = context.client().world
            if (world != null) {
                val mapstate = world.getMapState(payload.mapIdComponent)
                if (mapstate != null) {
                    (mapstate as MapStateAccessor).setPosition(payload.centerX, payload.centerZ)
                }
            }
        }
    }*/

    /*private fun bookShelfsyncSync(payload: BookShelfSyncPayload, context: ClientPlayNetworking.Context) {
        if (payload.mapIDs.size > 0) {
            context.client().execute {
                MapBookStateManager.INSTANCE.putClientMapBookState(
                    payload.bookID,
                    MapBookState(payload.mapIDs)
                )
            }
        }
    }*/
}
