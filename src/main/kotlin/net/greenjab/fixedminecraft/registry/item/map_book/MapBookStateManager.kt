package net.greenjab.fixedminecraft.registry.item.map_book

import com.google.common.collect.Maps
import net.minecraft.datafixer.DataFixTypes
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState

object MapBookStateManager {
    private val clientMapBooks: MutableMap<String, MapBookState> = Maps.newHashMap()

    fun getPersistentStateType(): PersistentState.Type<MapBookState> {
        return PersistentState.Type(
            { throw IllegalStateException("Should never create an empty map saved data - but for map books") },
            { nbt: NbtCompound -> createMapBookState(nbt) }, DataFixTypes.SAVED_DATA_MAP_DATA
        )
    }

    fun createMapBookState(nbt: NbtCompound): MapBookState {
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
        clientMapBooks[getMapBookName(id)] = state
    }

    fun getMapBookName(mapId: Int): String {
        return "fixedminecraft_map_book_$mapId"
    }
}
