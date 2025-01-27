package net.greenjab.fixedminecraft.registry.item.map_book


@JvmRecord
data class MapBookAdditionsComponent3(val additions: List<Int>) {
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
