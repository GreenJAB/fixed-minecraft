package net.greenjab.fixedminecraft.registry.item.map_book

import com.google.common.collect.Lists
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.map.MapIcon
import net.minecraft.item.map.MapState.PlayerUpdateTracker
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.world.PersistentState

class MapBookState() : PersistentState() {
    val mapIDs: ArrayList<Int> = ArrayList()
    val players: ArrayList<PlayerEntity> = ArrayList()

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
    fun update(player: PlayerEntity, stack: ItemStack) {
        if (!players.contains(player)) {
            println("0 " + mapIDs.toString())
            println("1 " + player.name.literalString)
            players.add(player)
        }

        for (i in players) {

            if (i!=player) {
                if (i.isRemoved || !i.getInventory().contains(stack) ) {
                    players.remove(i)
                }
            }
        }
        this.markDirty()
    }
}
