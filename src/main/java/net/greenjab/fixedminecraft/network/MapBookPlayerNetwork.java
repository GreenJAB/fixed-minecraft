package net.greenjab.fixedminecraft.network;

import java.util.ArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public class MapBookPlayerNetwork {
    public static final StreamCodec<FriendlyByteBuf, ArrayList<MapBookPlayer>> ARRAY_CODEC = new StreamCodec<>() {
        public @NonNull ArrayList<MapBookPlayer> decode(@NonNull FriendlyByteBuf byteBuf) {
            int length = VarInt.read(byteBuf);
            ArrayList<MapBookPlayer> array = new ArrayList<>();
            for(int j = 0; j < length; j++) {
                array.add(MapBookPlayer.fromPacket(byteBuf));
            }
            return array;
        }

        public void encode(@NonNull FriendlyByteBuf byteBuf, ArrayList<MapBookPlayer> array) {
            ArrayList<MapBookPlayer> array2 = (ArrayList<MapBookPlayer>) array.clone();
            VarInt.write(byteBuf, array.size());
            for (int i = 0; i < array.size();i++) {
                array2.get(i).toPacket(byteBuf);
            }
        }
    };
    public static final StreamCodec<FriendlyByteBuf, MapBookPlayer> SINGLE = new StreamCodec<>() {
        public @NonNull MapBookPlayer decode(@NonNull FriendlyByteBuf byteBuf) {
            return MapBookPlayer.fromPacket(byteBuf);
        }

        public void encode(@NonNull FriendlyByteBuf byteBuf, MapBookPlayer mapBookPlayer) {
            mapBookPlayer.toPacket(byteBuf);
        }
    };
}
