package net.greenjab.fixedminecraft.registry.item.map_book

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.greenjab.fixedminecraft.network.MapBookPlayer
import net.greenjab.fixedminecraft.network.MapBookSyncPayload
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState
import java.util.Arrays


class MapBookState() : PersistentState() {
    var players: ArrayList<MapBookPlayer> = ArrayList()
    val mapIDs: java.util.ArrayList<Int> = java.util.ArrayList()

    fun addPlayer(player: PlayerEntity) {
        val p = MapBookPlayer()
        p.setPlayer(player)
        players.add(p)
    }

    constructor(ids: IntArray?, players: ArrayList<MapBookPlayer>) : this() {
        mapIDs.clear()
        mapIDs.addAll(Arrays.stream(ids).boxed().toList())
        this.players.clear()
        for (player in players) {
            this.players.add(player)
        }
        this.markDirty()
    }

    fun sendData(server: MinecraftServer, id:Int) {
        for (player in players) {
            val SPE = server.playerManager.getPlayer(player.name)
            if (SPE != null) {
                var hold = false
                for (item in SPE.handItems) {
                    if (item.isOf(ItemRegistry.MAP_BOOK)) {
                        hold = true
                    }
                }
                if (hold) {
                    ServerPlayNetworking.send(SPE, MapBookSyncPayload(id,
                        mapIDs.stream().mapToInt { i: Int? -> i!! }.toArray(), players))
                }
            }
        }
        MapBookStateManager.INSTANCE.getMapBookState(server, id)?.players=ArrayList()
    }

    override fun writeNbt(nbt: NbtCompound, lookup: WrapperLookup): NbtCompound {
        if (mapIDs.isNotEmpty()) {
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
