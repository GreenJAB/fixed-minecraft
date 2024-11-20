package net.greenjab.fixedminecraft.registry.item.map_book

import net.greenjab.fixedminecraft.network.SyncHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState

class MapBookState() : PersistentState() {
    var mapIDs: ArrayList<Int> = ArrayList()
    var players: ArrayList<MapBookPlayer> = ArrayList()

    fun addPlayer(player: PlayerEntity) {
        val p = MapBookPlayer()
        p.setPlayer(player)
        players.add(p)
    }

    constructor(ids: IntArray) : this() {
        mapIDs.clear()
        mapIDs.addAll(ids.toList())
        this.markDirty()
    }

    fun sendData(server: MinecraftServer, id:Int) {
        for (player in players) {
            val SPE = server.playerManager.getPlayer(player.name)
            if (SPE != null) {
                //if (SPE.handItems.contains(ItemRegistry.MAP_BOOK.defaultStack)) {
                    SyncHandler.mapBookSync(SPE, id)
                //}
            }
        }
        MapBookStateManager.getMapBookState(server, id)?.players=ArrayList()
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
