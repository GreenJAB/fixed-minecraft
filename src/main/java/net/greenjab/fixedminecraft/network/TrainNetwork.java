package net.greenjab.fixedminecraft.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;

import java.util.ArrayList;
import java.util.UUID;

public class TrainNetwork {
    public static final PacketCodec<PacketByteBuf, ArrayList<UUID>> ARRAY_CODEC = new PacketCodec<>() {
        public ArrayList<UUID> decode(PacketByteBuf byteBuf) {
            int length = VarInts.read(byteBuf);
            ArrayList<UUID> array = new ArrayList<>();
            for(int j = 0; j < length; j++) {
                array.add(byteBuf.readUuid());
            }
            return array;
        }

        public void encode(PacketByteBuf byteBuf, ArrayList<UUID> array) {
            ArrayList<UUID> array2 = (ArrayList<UUID>) array.clone();
            VarInts.write(byteBuf, array.size());
            for (int i = 0; i < array.size();i++) {
                byteBuf.writeUuid(array2.get(i));
            }
        }
    };
}

