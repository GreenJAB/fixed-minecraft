package net.greenjab.fixedminecraft.registry.item.map_book

import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.world.PersistentState
import java.util.Arrays


class MapBookState() : PersistentState() {
    val mapIDs: java.util.ArrayList<Int> = java.util.ArrayList()

    constructor(ids: IntArray?) : this() {
        mapIDs.clear()
        mapIDs.addAll(Arrays.stream(ids).boxed().toList())
        this.markDirty()
    }

    override fun writeNbt(nbt: NbtCompound, lookup: WrapperLookup): NbtCompound {
        if (!mapIDs.isEmpty()) {
            nbt.putIntArray("mapIDs", this.mapIDs)
        }

        return nbt
    }

    fun fromNbt(nbt: NbtCompound): MapBookState {
        mapIDs.clear()
        val ids = nbt.getIntArray("mapIDs")
        mapIDs.addAll(Arrays.stream(ids).boxed().toList())
        return this
    }

    fun addMapID(id: Int) {
        mapIDs.add(id)
        this.markDirty()
    }


    fun update() {
        val temp: ArrayList<Int> = ArrayList()
        for (i in mapIDs) {
            if (!temp.contains(i)) {
                temp.add(i)
            }
        }
        mapIDs.clear()
        for (i in temp){
            mapIDs.add(i)
        }

        this.markDirty()
    }

}
