package net.greenjab.fixedminecraft.registry.item.map_book

import com.google.common.collect.Maps
import net.minecraft.datafixer.DataFixTypes
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState

object MapBookStateManager {
    private val clientMapBooks: MutableMap<String, MapBookState> = Maps.newHashMap()
    var currentBooks: ArrayList<Int> = ArrayList()

    private fun getPersistentStateType(): PersistentState.Type<MapBookState> {
        return PersistentState.Type(
            { throw IllegalStateException("Should never create an empty map saved data - but for map books") },
            { nbt: NbtCompound -> createMapBookState(nbt) }, DataFixTypes.SAVED_DATA_MAP_DATA
        )
    }

    private fun createMapBookState(nbt: NbtCompound): MapBookState {
        val state = MapBookState()
        state.fromNbt(nbt)
        return state
    }

    fun getMapBookState(server: MinecraftServer, id: Int?): MapBookState? {
        if (id == null) return null
        return server.overworld.persistentStateManager.get(getPersistentStateType(), getMapBookName(id))
    }

    fun putMapBookState(server: MinecraftServer, id: Int?, state: MapBookState?) {
        if (id == null) return
        server.overworld.persistentStateManager.set(getMapBookName(id), state)
    }

    fun getClientMapBookState(id: Int?): MapBookState? {
        if (id == null) return null
        return clientMapBooks[getMapBookName(id)]
    }

    fun putClientMapBookState(id: Int?, state: MapBookState?) {
        if (id == null || state == null) return
        val s = getMapBookName(id)
        if (!clientMapBooks.keys.contains(s)) {
            clientMapBooks[getMapBookName(id)] = state
        } else {
            clientMapBooks[getMapBookName(id)]?.mapIDs = state.mapIDs
        }
    }

    private fun getMapBookName(mapId: Int): String {
        return "fixedminecraft_map_book_$mapId"
    }
}
