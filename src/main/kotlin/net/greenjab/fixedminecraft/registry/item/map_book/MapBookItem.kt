package net.greenjab.fixedminecraft.registry.item.map_book

import net.greenjab.fixedminecraft.registry.ItemRegistry
import net.greenjab.fixedminecraft.network.MapBookOpenPayload
import net.greenjab.fixedminecraft.network.MapBookSyncPayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.MapIdComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.item.map.MapState
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.network.packet.Packet
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.Objects
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager

class MapBookItem(settings: Settings?) : Item(settings) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val blockState = context.world.getBlockState(context.blockPos)

        if (blockState.isIn(BlockTags.BANNERS)) {
            if (!context.world.isClient) {
                val mapStateData = this.getNearestMap(context.stack, context.world, context.blockPos.toCenterPos())
                val mapState: MapState? = mapStateData?.mapState
                if (mapState != null && !mapState.addBanner(context.world, context.blockPos)) {
                    return ActionResult.FAIL
                }
            }

            return ActionResult.SUCCESS
        } else {
            return super.useOnBlock(context)
        }
    }

    /*override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        if (world != null && !world.isClient()) {
            val player = user as ServerPlayerEntity
            val item = user.getStackInHand(hand)
            val otherHand = if (hand == Hand.MAIN_HAND) player.offHandStack else player.mainHandStack
            var openMap = true
            if (otherHand.isOf(Items.PAPER)) {
                if (this.addNewMapAtPos(item, world as ServerWorld, player.pos, 0)) {
                    if (!player.abilities.creativeMode) {
                        otherHand.decrement(1)
                    }

                    player.world.playSoundFromEntity(
                        null,
                        player,
                        SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                        player.soundCategory,
                        1.0f,
                        1.0f
                    )
                    openMap = false
                }
            }
            this.sendMapUpdates(player, item)
            mapBookSync(player, item)
            if (openMap && this.hasMapBookId(item)) {
                mapBookOpen(player, item)
            }
        }

        return super.use(world, user, hand)
    }*/

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): ActionResult {
        if (world != null && !world.isClient()) {
            val player = user as ServerPlayerEntity
            val item = user.getStackInHand(hand)
            val otherHand = if (hand == Hand.MAIN_HAND) player.offHandStack else player.mainHandStack

            var openMap = true
            if (getNearestMap(item, world as ServerWorld, player.pos)==null) {
                if (addNewMapAtPos(item, world, player.pos,0)) {
                    player.world.playSoundFromEntity(
                        null,
                        player,
                        SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                        player.soundCategory,
                        1.0f,
                        1.0f
                    )
                    openMap = false
                }
            } else if (otherHand.isOf(Items.PAPER)) {
                if (addNewMapAtPos(item, world, player.pos, 0)) {
                    if (!player.abilities.creativeMode) {
                        otherHand.decrement(1)
                    }
                    player.world.playSoundFromEntity(
                        null,
                        player,
                        SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                        player.soundCategory,
                        1.0f,
                        1.0f
                    )
                    openMap = false
                }
            } else if (otherHand.isOf(Items.FILLED_MAP)) {
                if (addNewMapID(item, otherHand, world)) {
                    if (!player.abilities.creativeMode) {
                        otherHand.decrement(1)
                    }
                    player.world.playSoundFromEntity(
                        null,
                        player,
                        SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                        player.soundCategory,
                        1.0f,
                        1.0f
                    )
                    openMap = false
                }
            } else {
                val hasPaper = getPaper(user)
                if (hasPaper.isOf(Items.PAPER)) {
                    if (addNewMapAtPos(item, world, player.pos, 0)) {
                        if (!player.abilities.creativeMode) {
                            hasPaper.decrement(1)
                        }
                        player.world.playSoundFromEntity(
                            null,
                            player,
                            SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                            player.soundCategory,
                            1.0f,
                            1.0f
                        )
                        openMap = false
                    }
                }
            }

            this.sendMapUpdates(player, item)
            mapBookSync(player, item)
            if (openMap && this.hasMapBookId(item)) {
                this.getMapBookState(item, world)?.update()
                mapBookOpen(player, item)
            }
        }
        return ActionResult.SUCCESS
        //return super.use(world, user, hand)
    }

    private fun getPaper(playerEntity: PlayerEntity): ItemStack {
        for (i in 0 until playerEntity.inventory.size()) {
            val item = playerEntity.inventory.getStack(i)
            if (item.isOf(Items.PAPER)) return item
        }
        return ItemStack.EMPTY
    }

    fun sendMapUpdates(player: ServerPlayerEntity, item: ItemStack) {
        for (mapStateData in this.getMapStates(item, player.world)) {
            mapStateData.mapState.getPlayerSyncData(player)
            val packet: Packet<*>? = mapStateData.mapState.getPlayerMarkerPacket(mapStateData.id, player)
            if (packet != null) {
                player.networkHandler.sendPacket(packet)
            }
        }
    }

    override fun inventoryTick(stack: ItemStack?, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        if (world != null && !world.isClient()) {
            if (stack != null && entity is PlayerEntity) {
                if (selected || entity.offHandStack == stack) {
                    // this.applyAdditions(stack, (ServerWorld)world);

                    for (mapStateData in this.getMapStates(stack, entity.getWorld())) {
                        mapStateData.mapState.update(entity as PlayerEntity?, stack)
                        if (!mapStateData.mapState.locked) {
                            if (this.getDistanceToEdgeOfMap(mapStateData.mapState, entity.getPos()) < 128.0) {
                                (Items.FILLED_MAP as FilledMapItem).updateColors(world, entity, mapStateData.mapState)
                            }
                        }
                    }
                    val id = getMapBookId(stack)
                    world.server?.let {
                        if (id != null) {
                            MapBookStateManager.INSTANCE.getMapBookState(it, id)?.addPlayer(entity)
                        }
                    }
                    if (!MapBookStateManager.INSTANCE.currentBooks.contains(id)) id?.let { MapBookStateManager.INSTANCE.currentBooks.add(it) }
                    this.sendMapUpdates(entity as ServerPlayerEntity, stack)
                    //mapBookSync(entity, stack)
                }
            }
        }
    }

    fun getMapStates(stack: ItemStack, world: World): ArrayList<MapStateData> {
        val list = ArrayList<MapStateData>()
        val mapBookState = getMapBookState(stack, world)

        if (mapBookState != null) {
            for (i in mapBookState.mapIDs) {
                val mapState = world.getMapState(MapIdComponent(i))
                if (mapState != null) {
                    list.add(MapStateData(MapIdComponent(i), mapState))
                }
            }
        }
        return list
    }

    private fun getMapBookState(stack: ItemStack, world: World): MapBookState? {
        val id = getMapBookId(stack) ?: return null

        if (world.isClient) {
            return MapBookStateManager.INSTANCE.getClientMapBookState(id)
        } else if (world.server != null) {
            return MapBookStateManager.INSTANCE.getMapBookState(world.server!!, id)
        }
        return null
    }

    fun getNearestMap(stack: ItemStack, world: World, pos: Vec3d): MapStateData? {
        var nearestDistance = Double.MAX_VALUE
        var nearestScale: Byte = 127
        var nearestMap: MapStateData? = null

        val mapStates: Iterator<MapStateData> = getMapStates(stack, world).iterator()

        while (true) {
            var mapStateData: MapStateData
            var distance: Double
            var roughlyEqual: Boolean
            do {
                do {
                    if (!mapStates.hasNext()) {
                        return nearestMap
                    }

                    mapStateData = mapStates.next()
                    distance = this.getDistanceToEdgeOfMap(mapStateData.mapState, pos)
                    if (distance < 0.0) {
                        distance = -1.0
                    }

                    roughlyEqual = abs(nearestDistance - distance) < 1.0
                } while (!(distance < nearestDistance) && !roughlyEqual)
            } while (roughlyEqual && (!(distance < 0.0) || mapStateData.mapState.scale >= nearestScale) && (!(distance > 0.0) || mapStateData.mapState.scale <= nearestScale))

            nearestDistance = distance
            nearestScale = mapStateData.mapState.scale
            nearestMap = mapStateData
        }
    }

    private fun getDistanceToEdgeOfMap(mapState: MapState, pos: Vec3d): Double {
        return max(
            abs(pos.x - mapState.centerX.toDouble()),
            abs(pos.z - mapState.centerZ.toDouble())
        ) - (64 * (1 shl mapState.scale.toInt())).toDouble()
    }

    private fun hasMapBookId(stack: ItemStack): Boolean {
        return stack.contains(DataComponentTypes.MAP_ID)
    }

    fun getMapBookId(stack: ItemStack): Int? {
        val mapIdComponent = stack.getOrDefault(DataComponentTypes.MAP_ID, null)
        return mapIdComponent?.id()
    }

    private fun allocateMapBookId(server: MinecraftServer): Int {
        val counts: MapBookIdCountsState = server.overworld.persistentStateManager.getOrCreate(
            MapBookIdCountsState.persistentStateType,
            MapBookIdCountsState.IDCOUNTS_KEY
        )
        val i = counts.nextMapBookId
        MapBookStateManager.INSTANCE.putMapBookState(server, i, MapBookState())
        return i
    }

    private fun setMapBookId(stack: ItemStack, id: Int) {
        stack.set(DataComponentTypes.MAP_ID, MapIdComponent(id))
    }

    private fun createMapBookState(stack: ItemStack, server: MinecraftServer): Int {
        val i = this.allocateMapBookId(server)
        this.setMapBookId(stack, i)
        return i
    }

    private fun getOrCreateMapBookState(stack: ItemStack, server: MinecraftServer): MapBookState? {
        val id = this.getMapBookId(stack)
        val state = if (id == null) null else MapBookStateManager.INSTANCE.getMapBookState(server, id)
        if (state != null) {
            return state
        } else {
            val i = this.createMapBookState(stack, server)
            return MapBookStateManager.INSTANCE.getMapBookState(server, i)
        }
    }

    private fun addNewMapAtPos(item: ItemStack, world: ServerWorld, pos: Vec3d, scale: Int): Boolean {
        val state = this.getOrCreateMapBookState(item, world.server)
        val nearestState = this.getNearestMap(item, world, pos)
        if (nearestState != null && nearestState.mapState.scale <= scale && !(this.getDistanceToEdgeOfMap(
                nearestState.mapState,
                pos
            ) > 0.0)
        ) {
            return false
        } else {
            val newMap = FilledMapItem.createMap(
                world,
                floor(pos.x).toInt(), floor(pos.z).toInt(), scale.toByte(), true, false
            )
            Objects.requireNonNull(newMap.get(DataComponentTypes.MAP_ID))?.let { state!!.addMapID(it.id) }
            return true
        }
    }

    private fun addNewMapID(item: ItemStack, filledmap: ItemStack, world: ServerWorld): Boolean {
        val mapId = filledmap.get(DataComponentTypes.MAP_ID) ?: return false
        val state = this.getOrCreateMapBookState(item, world.server)
        if (state != null) {
            if (!state.mapIDs.contains(mapId.id)) {
                Objects.requireNonNull(filledmap.get(DataComponentTypes.MAP_ID))?.let { state!!.addMapID(it.id) }
                return true
            }
        }
        return false

        /*val mapId = filledmap.get(DataComponentTypes.MAP_ID) ?: return false
        val maps = ArrayList<Int>()
        maps.add(mapId.id())
        if ((item.item as MapBookItem).hasInvalidAdditions(item, world, maps)) {
            var result = MapBookAdditionRecipe.AdditionResult(item, maps)
            val item2 = result.mapBook.copyWithCount(1)
            (item2.item as MapBookItem).setAdditions(item2, result.maps)
            return true
        }
        return false*/

        /*val state = getOrCreateMapBookState(item, world.server)
        val id = FilledMapItem.getMapId(filledmap)
        if (!state.mapIDs.contains(id)) {
            state.addMapID(id!!)
            return true
        }
        return false*/
    }

    override fun getName(stack: ItemStack?): Text {
        return if (stack != null && !this.hasMapBookId(stack)) {
            if (stack.contains(ItemRegistry.MAP_BOOK_ADDITIONS)) {
                Text.translatable("item.fixedminecraft.map_book_new")
            } else {
                Text.translatable("item.fixedminecraft.map_book_empty")
            }
        } else {
            super.getName(stack)
        }
    }

    override fun appendTooltip(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Text>, type: TooltipType) {
        if (stack != null) {
            var mapsCount =
                stack.getOrDefault(ItemRegistry.MAP_BOOK_ADDITIONS, MapBookAdditionsComponent.DEFAULT).additions.size
            val id = this.getMapBookId(stack)
            if (id != null) {
                // append tooltip is client-based, so its safe to get the client MapBookState
                val mapBookState = MapBookStateManager.INSTANCE.getClientMapBookState(id)

                if (mapBookState != null) {
                    mapsCount += mapBookState.mapIDs.size
                }

                tooltip.add(Text.translatable("item.fixedminecraft.map_book_id", (id + 1).toString()).formatted(Formatting.GRAY))
            }

            if (mapsCount > 0) {
                tooltip.add(Text.translatable("item.fixedminecraft.map_book_maps", mapsCount.toString()).formatted(Formatting.GRAY))
            }
        }
    }

    fun setAdditions(stack: ItemStack, additions: List<Int>) {
        stack.set<MapBookAdditionsComponent>(ItemRegistry.MAP_BOOK_ADDITIONS,
            MapBookAdditionsComponent(additions)
        )
    }

    private fun applyAdditions(stack: ItemStack, world: ServerWorld) {
        val additionsComponent = stack.getOrDefault<MapBookAdditionsComponent>(ItemRegistry.MAP_BOOK_ADDITIONS, null) ?: return
        stack.remove<Any>(ItemRegistry.MAP_BOOK_ADDITIONS)

        val additions = additionsComponent.additions
        if (!additions.isEmpty()) {
            val state = this.getOrCreateMapBookState(stack, world.server)

            for (id in additions) {
                val newState = world.getMapState(id?.let { MapIdComponent(it) }) ?: continue

                state!!.mapIDs.removeIf { existingID: Int? ->
                    val existingState = world.getMapState(MapIdComponent(existingID!!))
                    existingState == null || mapsAreSameLocation(newState, existingState)
                }

                if (id != null) {
                    state.addMapID(id)
                }
            }
        }
    }

    fun hasInvalidAdditions(stack: ItemStack, world: World, additions: List<Int>): Boolean {
        val mapBookState = getMapBookState(stack, world)

        for (i in additions.indices) {
            val additionA = additions[i]
            if (mapBookState != null && mapBookState.mapIDs.contains(additionA)) {
                return true
            }

            val mapA = world.getMapState(MapIdComponent(additionA)) ?: return true

            for (j in i + 1 until additions.size) {
                val additionB = additions[j]
                if (additionA == additionB) {
                    return true
                }

                val mapB = world.getMapState(MapIdComponent(additionB))
                if (mapB == null || mapsAreSameLocation(mapA, mapB)) {
                    return true
                }
            }
        }

        return false
    }

    private fun mapsAreSameLocation(mapA: MapState, mapB: MapState): Boolean {
        return mapA.scale == mapB.scale && mapA.centerX == mapB.centerX && mapA.centerZ == mapB.centerZ
    }

    override fun onCraftByPlayer(stack: ItemStack, world: World, player: PlayerEntity) {
        super.onCraftByPlayer(stack, world, player)
        if (!world.isClient) {
            mapBookSync(player as ServerPlayerEntity, stack)
        }
    }

    override fun onCraft(stack: ItemStack, world: World) {
        if (!world.isClient) {
            applyAdditions(stack, world as ServerWorld)
        }
    }

    fun mapBookOpen(player: ServerPlayerEntity, itemStack: ItemStack) {
        ServerPlayNetworking.send(player, MapBookOpenPayload(itemStack))
    }

    fun mapBookSync(player: ServerPlayerEntity, itemStack: ItemStack) {
        val payload: MapBookSyncPayload? = MapBookSyncPayload.of(player, itemStack)
        if (payload != null) {
            ServerPlayNetworking.send(player, payload)
        }
    }
}

/*
import net.greenjab.fixedminecraft.network.SyncHandler
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager.currentBooks
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager.getClientMapBookState
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager.getMapBookState
import net.minecraft.component.type.MapIdComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
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
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max


class MapBookItem(settings: Item.Settings?) : Item(settings) {
    private val MAP_BOOK_KEY = "fixedminecraft:map_book"
    private val ADDITIONS_KEY = "fixedminecraft:additions"

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        val blockPos = context.blockPos
        val blockState = world.getBlockState(blockPos)
        if (blockState.isIn(BlockTags.BANNERS)) {
            if (!world.isClient) {
                val mapState = getNearestMap(context.stack, world, blockPos.toCenterPos())?
                if (mapState != null && !mapState.addBanner(world, blockPos)) {
                    return ActionResult.FAIL
                }
            }
            return ActionResult.SUCCESS
        } else {
            return super.useOnBlock(context)
        }
    }

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): ActionResult {
        if (world != null && !world.isClient()) {
            val player = user as ServerPlayerEntity
            val item = user.getStackInHand(hand)
            val otherHand = if (hand == Hand.MAIN_HAND) player.offHandStack else player.mainHandStack

            var openMap = true
            if (getNearestMap(item, world as ServerWorld, player.pos)==null) {
                if (addNewMapAtPos(item, world, player.pos,true)) {
                    player.world.playSoundFromEntity(
                        null,
                        player,
                        SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                        player.soundCategory,
                        1.0f,
                        1.0f
                    )
                    openMap = false
                }
            } else if (otherHand.isOf(Items.PAPER)) {
                if (addNewMapAtPos(item, world, player.pos, true)) {
                    if (!player.abilities.creativeMode) {
                        otherHand.decrement(1)
                    }
                    player.world.playSoundFromEntity(
                        null,
                        player,
                        SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                        player.soundCategory,
                        1.0f,
                        1.0f
                    )
                    openMap = false
                }
            } else if (otherHand.isOf(Items.FILLED_MAP)) {
                if (addNewMapID(item, otherHand, world)) {
                    if (!player.abilities.creativeMode) {
                        otherHand.decrement(1)
                    }
                    player.world.playSoundFromEntity(
                        null,
                        player,
                        SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                        player.soundCategory,
                        1.0f,
                        1.0f
                    )
                    openMap = false
                }
            } else {
                val hasPaper = getPaper(user)
                if (hasPaper.isOf(Items.PAPER)) {
                    if (addNewMapAtPos(item, world, player.pos, false)) {
                        if (!player.abilities.creativeMode) {
                            hasPaper.decrement(1)
                        }
                        player.world.playSoundFromEntity(
                            null,
                            player,
                            SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                            player.soundCategory,
                            1.0f,
                            1.0f
                        )
                        openMap = false
                    }
                }
            }

            sendMapUpdates(player, item)
            if (openMap && getMapBookId(item) != null) {
                (if (world.isClient) {
                    MapBookStateManager.getClientMapBookState(getMapBookId(item))
                } else {
                    MapBookStateManager.getMapBookState(world.server, getMapBookId(item))
                })?.update()
                SyncHandler.onOpenMapBook(player, item)
            }
        }
        if (user != null) {
            return ActionResult.CONSUME
        }
        return super.use(world, user, hand)
    }

    private fun getPaper(playerEntity: PlayerEntity): ItemStack {
        for (i in 0 until playerEntity.inventory.size()) {
            val item = playerEntity.inventory.getStack(i)
            if (item.isOf(Items.PAPER)) return item
        }
        return ItemStack.EMPTY
    }

    private fun sendMapUpdates(player: ServerPlayerEntity, item: ItemStack) {
        for (mapStateData in getMapStates(item, player.world)) {
           // mapStateData.//.getPlayerSyncData(player)
            player.world.getMapState(mapStateData)?.getPlayerSyncData(player);
            val packet = player.world.getMapState(mapStateData)?.getPlayerMarkerPacket(mapStateData, player)
            if (packet != null) {
                player.networkHandler.sendPacket(packet)
            }
        }
    }

    override fun inventoryTick(stack: ItemStack?, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        if (world == null || world.isClient()) return
        if (stack == null || entity !is PlayerEntity) return
        if (selected || entity.offHandStack == stack) {
            applyAdditions(stack, world as ServerWorld)
            val m = getMapStates(stack, entity.world)
            for (mapStateData in m) {
                world.getMapState(mapStateData)?.update(entity, stack)

                if (!world.getMapState(mapStateData)?.locked!! && world.getMapState(mapStateData)
                        ?.let { getDistanceToEdgeOfMap(it, entity.pos) }!! < 128) {
                    (Items.FILLED_MAP as FilledMapItem).updateColors(world, entity, world.getMapState(mapStateData))
                }
            }
        }
        val id = getMapBookId(stack)
        world.server?.let { MapBookStateManager.getMapBookState(it, id)?.addPlayer(entity) }
        if (!currentBooks.contains(id)) id?.let { currentBooks.add(it) }
        sendMapUpdates(entity as ServerPlayerEntity, stack)
    }

    fun getMapStates(stack: ItemStack, world: World): ArrayList<MapStateData> {
        val list = ArrayList<MapStateData>()
        val mapBookState: MapBookState = getMapBookState(stack, world)

        if (mapBookState != null) {
            for (i in mapBookState.mapIDs) {
                val mapState = world.getMapState(MapIdComponent(i!!))
                if (mapState != null) {
                    list.add(MapStateData(MapIdComponent(i!!), mapState))
                }
            }
        }
        return list
    }

    private fun getMapBookState(stack: ItemStack, world: World): MapBookState? {
        val id = getMapBookId(stack) ?: return null

        if (world.isClient) {
            return getClientMapBookState(id)
        } else if (world.server != null) {
            return getMapBookState(world.server!!, id)
        }
        return null
    }

    fun getNearestMap(stack: ItemStack, world: World, pos: Vec3d): MapStateData? {
        var nearestDistance = Double.MAX_VALUE
        var nearestScale: Byte = 127
        var nearestMap: MapStateData? = null

        val mapStates: Iterator<MapStateData> = getMapStates(stack, world).iterator()

        while (true) {
            var mapStateData: MapStateData
            var distance: Double
            var roughlyEqual: Boolean
            do {
                do {
                    if (!mapStates.hasNext()) {
                        return nearestMap
                    }

                    mapStateData = mapStates.next()
                    distance = this.getDistanceToEdgeOfMap(mapStateData.mapState(), pos)
                    if (distance < 0.0) {
                        distance = -1.0
                    }

                    roughlyEqual = abs(nearestDistance - distance) < 1.0
                } while (!(distance < nearestDistance) && !roughlyEqual)
            } while (roughlyEqual && (!(distance < 0.0) || mapStateData.mapState().scale >= nearestScale) && (!(distance > 0.0) || mapStateData.mapState().scale <= nearestScale))

            nearestDistance = distance
            nearestScale = mapStateData.mapState().scale
            nearestMap = mapStateData
        }
    }

    private fun getDistanceToEdgeOfMap(mapState: MapState, pos: Vec3d): Double {
        // get signed distance to edge of map
        // so the edge of the map is 0, inside is negative and outside is positive
        // note the distance does not round the corners like a proper sdf would
        return max(abs(pos.x - mapState.centerX), abs(pos.z - mapState.centerZ)) - 64 * (1 shl mapState.scale.toInt())
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
        val counts = server.overworld?.persistentStateManager?.getOrCreate(
            MapBookIdCountsState.persistentStateType,
            MapBookIdCountsState.IDCOUNTS_KEY
        )
        val i = counts!!.nextMapBookId
        MapBookStateManager.putMapBookState(server, i, mapBookState)
        return i
    }

    private fun setMapBookId(stack: ItemStack, id: Int) {
        stack.getOrCreateNbt().putInt(MAP_BOOK_KEY, id)
    }

    private fun createMapBookState(stack: ItemStack, server: MinecraftServer): Int {
        val i = allocateMapBookId(server)
        setMapBookId(stack, i)
        return i
    }

    private fun getOrCreateMapBookState(stack: ItemStack, server: MinecraftServer): MapBookState {
        val state = MapBookStateManager.getMapBookState(server, getMapBookId(stack))
        if (state != null) return state
        val i = createMapBookState(stack, server)
        return MapBookStateManager.getMapBookState(server, i)!!
    }

    private fun addNewMapAtPos(item: ItemStack, world: ServerWorld, pos: Vec3d, otherhand: Boolean): Boolean {
        val state = getOrCreateMapBookState(item, world.server)

        val nearestState = getNearestMap(item, world, pos)
        // make a new map if the book has no maps, the position is outside a map, or the map the position is in has a larger scale
        if (nearestState == null || (nearestState.mapState.scale > 0 && otherhand) || getDistanceToEdgeOfMap(
                nearestState.mapState,
                pos
            ) > 0
        ) {
            val newMap = FilledMapItem.createMap(world, floor(pos.x).toInt(), floor(pos.z).toInt(), 0, true, false)
            state.addMapID(FilledMapItem.getMapId(newMap)!!)
            return true
        }
        return false
    }

    private fun addNewMapID(item: ItemStack, filledmap: ItemStack, world: ServerWorld): Boolean {
        val state = getOrCreateMapBookState(item, world.server)
        val id = FilledMapItem.getMapId(filledmap)
        if (!state.mapIDs.contains(id)) {
            state.addMapID(id!!)
            return true
        }
        return false
    }

    override fun getName(stack: ItemStack?): Text {
        if (stack != null && getMapBookId(stack) == null) {
            if (stack.getOrCreateNbt().contains(ADDITIONS_KEY)) {
                return Text.translatable("item.fixedminecraft.map_book_new")
            }
            return Text.translatable("item.fixedminecraft.map_book_empty")
        }
        return super.getName(stack)
    }

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        if (stack == null) return
        val id = getMapBookId(stack)

        var mapsCount = stack.getOrCreateNbt().getIntArray((ADDITIONS_KEY)).size
        if (id != null) {
            val mapBookState =
                if (world == null || world.isClient) MapBookStateManager.getClientMapBookState(id) else MapBookStateManager.getMapBookState(
                    world.server!!,
                    id
                )
            mapsCount += mapBookState?.mapIDs?.count() ?: 0

            tooltip!!.add(
                Text.translatable("item.fixedminecraft.map_book_id").append(ScreenTexts.SPACE).append((id + 1).toString())
                    .formatted(Formatting.GRAY)
            )
        }
        if (mapsCount > 0) {
            tooltip!!.add(
                Text.translatable("item.fixedminecraft.map_book_maps").append(ScreenTexts.SPACE).append(mapsCount.toString())
                    .formatted(Formatting.GRAY)
            )
        }
    }

    fun setAdditions(stack: ItemStack, additions: List<Int>) {
        stack.getOrCreateNbt().putIntArray(ADDITIONS_KEY, additions)
    }

    private fun applyAdditions(stack: ItemStack, world: ServerWorld) {
        val nbt = stack.getOrCreateNbt()
        if (!nbt.contains(ADDITIONS_KEY)) return

        val additions = nbt.getIntArray(ADDITIONS_KEY)
        nbt.remove(ADDITIONS_KEY)
        if (additions.isEmpty()) return

        val state = getOrCreateMapBookState(stack, world.server)
        for (id in additions) {
            state.addMapID(id)
        }
    }

    override fun isEnchantable(stack: ItemStack?): Boolean {
        return true
    }
}
*/
