package net.greenjab.fixedminecraft.items.map_book

import net.greenjab.fixedminecraft.network.SyncHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.NetworkSyncedItem
import net.minecraft.item.map.MapState
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.abs
import kotlin.math.max

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
            val packet = mapStateData.mapState.getPlayerMarkerPacket(mapStateData.id, player)
            if (packet != null) {
                player.networkHandler.sendPacket(packet)
            }
        }
    }

    override fun inventoryTick(stack: ItemStack?, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        if (world == null || world.isClient()) return
        if (stack == null || entity !is PlayerEntity) return
        if (!selected && entity.offHandStack != stack) return

        for (mapStateData in getMapStates(stack, entity.world)) {
            mapStateData.mapState.update(entity, stack)

            if (!mapStateData.mapState.locked && getDistanceToEdgeOfMap(mapStateData.mapState, entity) < 128) {
                (Items.FILLED_MAP as FilledMapItem).updateColors(world, entity, mapStateData.mapState)
            }
        }
        sendMapUpdates(entity as ServerPlayerEntity, stack)
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

    fun getNearestMap(stack: ItemStack, world: World?, player: PlayerEntity): MapStateData? {
        //get nearest map, if inside multiple maps, choose smallest resolution
        var nearestDistance = Double.MAX_VALUE
        var nearestScale: Byte = Byte.MAX_VALUE
        var nearestMap: MapStateData? = null

        for (mapStateData in getMapStates(stack, world)) {
            var distance = getDistanceToEdgeOfMap(mapStateData.mapState, player)
            if (distance < 0) distance = -1.0

            val roughlyEqual = abs(nearestDistance-distance) < 1
            if (distance < nearestDistance || roughlyEqual) {
                //if two maps are tied, take the smaller one if inside, or the larger one if outside
                if (!roughlyEqual || (distance < 0 && mapStateData.mapState.scale < nearestScale) || (distance > 0 && mapStateData.mapState.scale > nearestScale)) {
                    nearestDistance = distance
                    nearestScale = mapStateData.mapState.scale
                    nearestMap = mapStateData
                }
            }
        }

        return nearestMap
    }

    fun getDistance(mapState: MapState, player: PlayerEntity): Double {
        return player.pos.distanceTo(Vec3d(mapState.centerX.toDouble(), player.y, mapState.centerZ.toDouble()))
    }

    fun getDistanceToEdgeOfMap(mapState: MapState, player: PlayerEntity): Double {
        // get signed distance to edge of map
        // so the edge of the map is 0, inside is negative and outside is positive
        // note the distance does not round the corners like a proper sdf would
        return max(abs(player.pos.x-mapState.centerX), abs(player.pos.z-mapState.centerZ)) - 64*(1 shl mapState.scale.toInt())
    }
}