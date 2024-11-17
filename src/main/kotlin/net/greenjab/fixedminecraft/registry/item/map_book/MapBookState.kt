package net.greenjab.fixedminecraft.registry.item.map_book

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.PersistentState
import java.util.Optional

class MapBookState() : PersistentState() {
    /*class MapPlayer {
        var name: String  = ""
        var x: Double = 0.0
        var y: Double = 0.0
        var rot: Double = 0.0

        fun toPacket(buf: PacketByteBuf) {
            buf.writeString(name)
            buf.writeDouble(x)
            buf.writeDouble(y)
            buf.writeDouble(rot)
        }

        fun fromPacket(buf: PacketByteBuf): AdvancementDisplay {
            val text = buf.readUnlimitedText()
            val text2 = buf.readUnlimitedText()
            val itemStack = buf.readItemStack()
            val advancementFrame = buf.readEnumConstant(AdvancementFrame::class.java)
            val i = buf.readInt()
            val optional = if ((i and 1) != 0) Optional.of(buf.readIdentifier()) else Optional.empty()
            val bl = (i and 2) != 0
            val bl2 = (i and 4) != 0
            val advancementDisplay = AdvancementDisplay(itemStack, text, text2, optional, advancementFrame, bl, false, bl2)
            advancementDisplay.setPos(buf.readFloat(), buf.readFloat())
            return advancementDisplay
        }
    }*/
    val mapIDs: ArrayList<Int> = ArrayList()
    var players: ArrayList<MapBookPlayer> = ArrayList()

    fun addPlayer(player: PlayerEntity) {
        val p = MapBookPlayer()
        p.setPlayer(player)
        players.add(p)
        println("xx2 " + players.size)
    }
    /*var playerNames: ArrayList<String> = ArrayList()
    var playerXs: ArrayList<Double> = ArrayList()
    var playerYs: ArrayList<Double> = ArrayList()
    var playerRots: ArrayList<Double> = ArrayList()
     */

    /*fun getPlayerNames(): ArrayList<String> {
        val playerNames: ArrayList<String> = ArrayList()
        for (p in players) {
            playerNames.add(p.name)
        }
        return playerNames
    }

    fun getPlayerXs(): ArrayList<Double> {
        val playerNames: ArrayList<Double> = ArrayList()
        for (p in players) {
            playerNames.add(p.x)
        }
        return playerNames
    }

    fun getPlayerYs(): ArrayList<Double> {
        val playerNames: ArrayList<Double> = ArrayList()
        for (p in players) {
            playerNames.add(p.y)
        }
        return playerNames
    }

    fun getPlayerRots(): ArrayList<Double> {
        val playerNames: ArrayList<Double> = ArrayList()
        for (p in players) {
            playerNames.add(p.rot)
        }
        return playerNames
    }*/

    constructor(ids: IntArray) : this() {
        mapIDs.clear()
        mapIDs.addAll(ids.toList())
        this.markDirty()
    }
    constructor(ids: IntArray, players2: ArrayList<MapBookPlayer>?) : this() {
        mapIDs.clear()
        mapIDs.addAll(ids.toList())
        players.clear()
        if (players2 != null) {
            players.addAll(players2)
        }
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
