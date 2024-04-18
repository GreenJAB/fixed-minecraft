package net.greenjab.fixedminecraft.items.map_book

import net.greenjab.fixedminecraft.network.SyncHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.NetworkSyncedItem
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class MapBookItem(settings: Settings?) : NetworkSyncedItem(settings) {
    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if (world != null && !world.isClient()) {
            val player = user as ServerPlayerEntity
            val item = user.getStackInHand(hand)
            sendMapUpdates(player, item)
            SyncHandler.onOpenMapBook(player, item)
        }
        return super.use(world, user, hand)
    }

    fun sendMapUpdates(player: ServerPlayerEntity, item: ItemStack) {
        for (i in 1..16) {
            val mapState = FilledMapItem.getMapState(i, player.world)
            if (mapState != null) {
                mapState.getPlayerSyncData(player)
                player.networkHandler.sendPacket(mapState.getPlayerMarkerPacket(i, player))
            }
        }
    }
}