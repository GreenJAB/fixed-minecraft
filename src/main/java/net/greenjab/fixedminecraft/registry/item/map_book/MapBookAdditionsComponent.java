package net.greenjab.fixedminecraft.registry.item.map_book;

import com.mojang.serialization.Codec;
import net.greenjab.fixedminecraft.network.IntArray;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.ArrayList;
import java.util.List;

public record MapBookAdditionsComponent(List<Integer> additions) {
    public static final MapBookAdditionsComponent DEFAULT = new MapBookAdditionsComponent(new ArrayList<>());

    public static final Codec<MapBookAdditionsComponent> CODEC = Codec.INT.listOf().xmap(MapBookAdditionsComponent::new, MapBookAdditionsComponent::additions);
    public static final PacketCodec<RegistryByteBuf, MapBookAdditionsComponent> PACKET_CODEC = PacketCodec.tuple(
            IntArray.LIST_CODEC,
            MapBookAdditionsComponent::additions,
            MapBookAdditionsComponent::new
    );
}
