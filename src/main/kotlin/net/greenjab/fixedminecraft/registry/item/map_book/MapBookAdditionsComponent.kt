package net.greenjab.fixedminecraft.registry.item.map_book

import com.mojang.serialization.Codec
import net.greenjab.fixedminecraft.network.IntArray
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec


@JvmRecord
data class MapBookAdditionsComponent(val additions: List<Int>) {
   /* companion object {
        val DEFAULT: MapBookAdditionsComponent = MapBookAdditionsComponent(ArrayList())

        val CODEC: Codec<MapBookAdditionsComponent> = Codec.INT.listOf().xmap({ additions: List<Int> ->
            MapBookAdditionsComponent(
                additions
            )
        }, MapBookAdditionsComponent::additions)
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, MapBookAdditionsComponent> = PacketCodec.tuple(
            IntArray.LIST_CODEC,
            MapBookAdditionsComponent::additions
        ) { additions: List<Int> ->
            MapBookAdditionsComponent(
                additions
            )
        }
    }*/
}
