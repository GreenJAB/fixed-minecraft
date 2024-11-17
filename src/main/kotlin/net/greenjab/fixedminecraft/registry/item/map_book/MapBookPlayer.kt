package net.greenjab.fixedminecraft.registry.item.map_book

import net.minecraft.advancement.AdvancementDisplay
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import java.util.Optional

class MapBookPlayer {
        var name: String  = ""
        var x: Double = 0.0
        var z: Double = 0.0
        var yaw: Float = 0.0f
        var dimension: String = ""


    fun setPlayer(player: PlayerEntity) {
        this.name = player.name.literalString.toString()
        this.x = player.x
        this.z = player.z
        this.yaw = player.yaw
        this.dimension = player.world.dimensionKey.toString()
    }

        fun toPacket(buf: PacketByteBuf) {
            buf.writeString(name)
            buf.writeDouble(x)
            buf.writeDouble(z)
            buf.writeFloat(yaw)
            buf.writeString(dimension)
        }

         fun fromPacket(buf: PacketByteBuf): MapBookPlayer {
            val p = MapBookPlayer()
            p.name = buf.readString()
            p.x = buf.readDouble()
            p.z = buf.readDouble()
            p.yaw = buf.readFloat()
            p.dimension = buf.readString()
            return p
        }
    }
