package net.greenjab.fixedminecraft.registry.item.map_book

import com.google.common.collect.Lists
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.map.MapIcon
import net.minecraft.item.map.MapState.PlayerUpdateTracker
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
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
    fun update() {
        var temp: ArrayList<Int> = ArrayList()
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
