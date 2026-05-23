package net.greenjab.fixedminecraft.registry.item.map_book;

import com.mojang.serialization.Codec;
import net.greenjab.fixedminecraft.network.IntArray;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import java.util.ArrayList;
import java.util.List;

public record MapBookAdditionsComponent(List<Integer> additions) {
    public static final MapBookAdditionsComponent DEFAULT = new MapBookAdditionsComponent(new ArrayList<>());

    public static final Codec<MapBookAdditionsComponent> CODEC = Codec.INT.listOf().xmap(MapBookAdditionsComponent::new, MapBookAdditionsComponent::additions);
    public static final StreamCodec<RegistryFriendlyByteBuf, MapBookAdditionsComponent> PACKET_CODEC = StreamCodec.composite(
            IntArray.LIST_CODEC,
            MapBookAdditionsComponent::additions,
            MapBookAdditionsComponent::new
    );
}
