package net.greenjab.fixedminecraft.registry.item.map_book

import net.minecraft.nbt.NbtCompound
import net.minecraft.world.PersistentState

class MapBookState() : PersistentState() {
    val mapIDs: ArrayList<Int> = ArrayList()

    constructor(ids: IntArray) : this() {
        mapIDs.clear()
        mapIDs.addAll(ids.toList())
        this.markDirty()
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        if (mapIDs.isNotEmpty()) {
            nbt.putIntArray("mapIDs", mapIDs)
        }
        return nbt
    }

    fun fromNbt(nbt: NbtCompound): MapBookState {
        mapIDs.clear()
        mapIDs.addAll(nbt.getIntArray("mapIDs").toList())
        return this
    }

    fun addMapID(id: Int) {
        mapIDs.add(id)
        this.markDirty()
    }
}
