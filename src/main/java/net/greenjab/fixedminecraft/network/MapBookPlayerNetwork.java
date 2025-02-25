package net.greenjab.fixedminecraft.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;

import java.util.ArrayList;

public class MapBookPlayerNetwork {
    //why does this not exist? surely it exists somewhere
    public static final PacketCodec<PacketByteBuf, ArrayList<MapBookPlayer>> ARRAY_CODEC = new PacketCodec<>() {
        public ArrayList<MapBookPlayer> decode(PacketByteBuf byteBuf) {
            int length = VarInts.read(byteBuf);
            ArrayList<MapBookPlayer> array = new ArrayList<>();
            for(int j = 0; j < length; j++) {
                array.add(MapBookPlayer.fromPacket(byteBuf));
            }
            return array;
        }

        public void encode(PacketByteBuf byteBuf, ArrayList<MapBookPlayer> array) {
            VarInts.write(byteBuf, array.size());
            for (MapBookPlayer i : array) {
                i.toPacket(byteBuf);
            }
        }
    };
}
