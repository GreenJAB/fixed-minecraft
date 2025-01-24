package net.greenjab.fixedminecraft.registry.item.map_book;

import com.mojang.serialization.Codec;
import net.greenjab.fixedminecraft.network.IntArray;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.ArrayList;
import java.util.List;

public record MapBookAdditionsComponent2(List<Integer> additions) {
    public static final MapBookAdditionsComponent2 DEFAULT = new MapBookAdditionsComponent2(new ArrayList<>());

    public static final Codec<MapBookAdditionsComponent2> CODEC = Codec.INT.listOf().xmap(MapBookAdditionsComponent2::new, MapBookAdditionsComponent2::additions);
    public static final PacketCodec<RegistryByteBuf, MapBookAdditionsComponent2> PACKET_CODEC = PacketCodec.tuple(
            IntArray.LIST_CODEC,
            MapBookAdditionsComponent2::additions,
            MapBookAdditionsComponent2::new
    );
}
