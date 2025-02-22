package net.greenjab.fixedminecraft.registry.item.map_book

import net.greenjab.fixedminecraft.network.MapBookPlayer
import net.minecraft.datafixer.DataFixTypes
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState


class MapBookStateManager private constructor() {

    var currentBooks: ArrayList<Int> = ArrayList()

    val persistentStateType: PersistentState.Type<MapBookState>
        get() = PersistentState.Type(
            { throw IllegalStateException("Should never create an empty map saved data - but for map books") },
            { nbt: NbtCompound, lookup: WrapperLookup? ->
                INSTANCE.createMapBookState(
                    nbt
                )
            },
            DataFixTypes.SAVED_DATA_MAP_DATA
        )

    fun createMapBookState(nbt: NbtCompound): MapBookState {
        return MapBookState().fromNbt(nbt)
    }

    fun getMapBookState(server: MinecraftServer, id: Int): MapBookState? {
        return server.overworld.persistentStateManager.get(this.persistentStateType, this.getMapBookName(id))
    }

    fun putMapBookState(server: MinecraftServer, id: Int, state: MapBookState) {
        server.overworld.persistentStateManager[getMapBookName(id)] = state
    }

    fun getClientMapBookState(id: Int): MapBookState? {
        return clientMapBooks[getMapBookName(id)]
    }

    fun putClientMapBookState(id: Int, state: MapBookState) {
        clientMapBooks[getMapBookName(id)] = state
    }

    fun getMapBookName(mapId: Int): String {
        return "fixedminecraft_map_book_$mapId"
    }

    companion object {
        val INSTANCE: MapBookStateManager = MapBookStateManager()
        private val clientMapBooks: MutableMap<String, MapBookState> = HashMap()
    }
}
