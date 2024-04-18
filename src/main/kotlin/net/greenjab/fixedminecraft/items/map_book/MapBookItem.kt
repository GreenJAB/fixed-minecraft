package net.greenjab.fixedminecraft.items.map_book

import net.greenjab.fixedminecraft.network.SyncHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.NetworkSyncedItem
import net.minecraft.item.map.MapState
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d
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
        for (mapStateData in getMapStates(item, player.world)) {
            mapStateData.mapState.getPlayerSyncData(player)
            player.networkHandler.sendPacket(mapStateData.mapState.getPlayerMarkerPacket(mapStateData.id, player))
        }
    }

    override fun inventoryTick(stack: ItemStack?, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        if (world == null || world.isClient()) return
        if (stack == null || entity !is PlayerEntity) return
        if (!selected && entity.offHandStack != stack) return

        //for (mapStateData in getMapStates(stack, entity.world)) {
        //    if (!mapStateData.mapState.locked) {
        //        (Items.FILLED_MAP as FilledMapItem).updateColors(world, entity, mapStateData.mapState);
        //    }
        //}
        //sendMapUpdates(entity as ServerPlayerEntity, stack)
    }

    fun getMapStates(stack: ItemStack, world: World?): ArrayList<MapStateData> {
        val list = ArrayList<MapStateData>()
        for (i in 1..16) {
            val mapState = FilledMapItem.getMapState(i, world)
            if (mapState != null) {
                list.add(MapStateData(i, mapState))
            }
        }
        return list
    }

    fun getNearestMap(stack: ItemStack, world: World?, player: PlayerEntity): Int {
        var nearestDistance = -1.0
        var nearestMap = 0

        for (mapStateData in getMapStates(stack, world)) {
            val distance = getDistance(mapStateData.mapState, player)
            if (distance < nearestDistance || nearestDistance < 0) {
                nearestDistance = distance
                nearestMap = mapStateData.id
            }
        }

        return nearestMap
    }

    fun getDistance(mapState: MapState, player: PlayerEntity): Double {
        return player.pos.distanceTo(Vec3d(mapState.centerX.toDouble(), player.y, mapState.centerZ.toDouble()))
    }
}