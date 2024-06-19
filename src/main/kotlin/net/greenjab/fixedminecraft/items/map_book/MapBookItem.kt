package net.greenjab.fixedminecraft.items.map_book

import net.greenjab.fixedminecraft.network.SyncHandler
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.item.NetworkSyncedItem
import net.minecraft.item.map.MapState
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.tag.BlockTags
import net.minecraft.screen.ScreenTexts
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

class MapBookItem(settings: Settings?) : NetworkSyncedItem(settings) {
    private val MAP_BOOK_KEY = "fixedminecraft:map_book"

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val blockState = context.world.getBlockState(context.blockPos)
        if (blockState.isIn(BlockTags.BANNERS)) {
            if (!context.world.isClient) {
                val mapState = getNearestMap(context.stack, context.world, context.blockPos.toCenterPos())?.mapState
                if (mapState != null && !mapState.addBanner(context.world, context.blockPos)) {
                    return ActionResult.FAIL
                }
            }
            return ActionResult.success(context.world.isClient)
        } else {
            return super.useOnBlock(context)
        }
    }

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if (world != null && !world.isClient()) {
            val player = user as ServerPlayerEntity
            val item = user.getStackInHand(hand)
            val otherHand = if (hand == Hand.MAIN_HAND) player.offHandStack else player.mainHandStack

            var openMap = true

            if (otherHand.isOf(Items.PAPER)) {
                if (addNewMapAtPos(item, world as ServerWorld, player.pos, 0)) {
                    if (!player.abilities.creativeMode) {
                        otherHand.decrement(1)
                    }
                    player.world.playSoundFromEntity(null, player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, player.soundCategory, 1.0f, 1.0f)
                    openMap = false
                }
            }

            sendMapUpdates(player, item)
            SyncHandler.mapBookSync(player, item)
            if (openMap && getMapBookId(item) != null) {
                SyncHandler.onOpenMapBook(player, item)
            }
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

            if (!mapStateData.mapState.locked && getDistanceToEdgeOfMap(mapStateData.mapState, entity.pos) < 128) {
                (Items.FILLED_MAP as FilledMapItem).updateColors(world, entity, mapStateData.mapState)
            }
        }
        sendMapUpdates(entity as ServerPlayerEntity, stack)
    }

    fun getMapStates(stack: ItemStack, world: World?): ArrayList<MapStateData> {
        val list = ArrayList<MapStateData>()
        if (world == null) return list

        val mapBookState = if (world.isClient) {
            MapBookStateManager.getClientMapBookState(getMapBookId(stack))
        } else {
            MapBookStateManager.getMapBookState(world.server!!, getMapBookId(stack))
        }

        if (mapBookState == null) return list

        for (i in mapBookState.mapIDs) {
            val mapState = FilledMapItem.getMapState(i, world)
            if (mapState != null) {
                list.add(MapStateData(i, mapState))
            }
        }
        return list
    }

    fun getNearestMap(stack: ItemStack, world: World, pos: Vec3d): MapStateData? {
        // get nearest map, if inside multiple maps, choose smallest resolution
        var nearestDistance = Double.MAX_VALUE
        var nearestScale: Byte = Byte.MAX_VALUE
        var nearestMap: MapStateData? = null

        for (mapStateData in getMapStates(stack, world)) {
            var distance = getDistanceToEdgeOfMap(mapStateData.mapState, pos)
            if (distance < 0) distance = -1.0

            val roughlyEqual = abs(nearestDistance-distance) < 1
            if (distance < nearestDistance || roughlyEqual) {
                // if two maps are tied, take the smaller one if inside, or the larger one if outside
                if (!roughlyEqual || (distance < 0 && mapStateData.mapState.scale < nearestScale) || (distance > 0 && mapStateData.mapState.scale > nearestScale)) {
                    nearestDistance = distance
                    nearestScale = mapStateData.mapState.scale
                    nearestMap = mapStateData
                }
            }
        }

        return nearestMap
    }

    fun getDistanceToEdgeOfMap(mapState: MapState, pos: Vec3d): Double {
        // get signed distance to edge of map
        // so the edge of the map is 0, inside is negative and outside is positive
        // note the distance does not round the corners like a proper sdf would
        return max(abs(pos.x-mapState.centerX), abs(pos.z-mapState.centerZ)) - 64*(1 shl mapState.scale.toInt())
    }

    fun getMapBookId(stack: ItemStack): Int? {
        val nbtCompound = stack.nbt
        return if (nbtCompound != null && nbtCompound.contains(
                MAP_BOOK_KEY,
                NbtElement.NUMBER_TYPE.toInt()
            )
        ) Integer.valueOf(nbtCompound.getInt(MAP_BOOK_KEY)) else null
    }

    private fun allocateMapBookId(server: MinecraftServer): Int {
        val mapBookState = MapBookState()
        val counts = server.overworld?.persistentStateManager?.getOrCreate(MapBookIdCountsState.persistentStateType, MapBookIdCountsState.IDCOUNTS_KEY)
        val i = counts!!.nextMapBookId
        MapBookStateManager.putMapBookState(server, i, mapBookState)
        return i
    }

    private fun setMapBookId(stack: ItemStack, id: Int) {
        stack.getOrCreateNbt().putInt(MAP_BOOK_KEY, id)
    }

    private fun createMapBookState(stack: ItemStack, server: MinecraftServer) : Int {
        val i = allocateMapBookId(server)
        setMapBookId(stack, i)
        return i
    }

    private fun getOrCreateMapBookState(stack: ItemStack, server: MinecraftServer) : MapBookState {
        val state = MapBookStateManager.getMapBookState(server, getMapBookId(stack))
        if (state != null) return state
        val i = createMapBookState(stack, server)
        return MapBookStateManager.getMapBookState(server, i)!!
    }

    private fun addNewMapAtPos(item: ItemStack, world: ServerWorld, pos: Vec3d, scale: Int) : Boolean {
        val state = getOrCreateMapBookState(item, world.server)

        val nearestState = getNearestMap(item, world, pos)
        // make a new map if the book has no maps, the position is outside a map, or the map the position is in has a larger scale
        if (nearestState == null || nearestState.mapState.scale > scale || getDistanceToEdgeOfMap(nearestState.mapState, pos) > 0) {
            val newMap = FilledMapItem.createMap(world, floor(pos.x).toInt(), floor(pos.z).toInt(), scale.toByte(), true, false)
            state.addMapID(FilledMapItem.getMapId(newMap)!!)
            return true
        }
        return false
    }

    override fun getName(stack: ItemStack?): Text {
        if (stack != null && getMapBookId(stack) == null) return Text.translatable("item.fixedminecraft.map_book_empty")
        return super.getName(stack)
    }

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        if (stack == null) return
        val id = getMapBookId(stack) ?: return
        val mapBookState = if (world == null || world.isClient) MapBookStateManager.getClientMapBookState(id) else MapBookStateManager.getMapBookState(world.server!!, id)
        val mapsCount = mapBookState?.mapIDs?.count() ?: 0

        tooltip!!.add(Text.translatable("item.fixedminecraft.map_book_id").append(ScreenTexts.SPACE).append((id+1).toString()).formatted(Formatting.GRAY))
        tooltip.add(Text.translatable("item.fixedminecraft.map_book_maps").append(ScreenTexts.SPACE).append(mapsCount.toString()).formatted(Formatting.GRAY))
    }
}
