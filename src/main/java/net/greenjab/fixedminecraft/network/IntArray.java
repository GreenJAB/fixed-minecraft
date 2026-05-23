package net.greenjab.fixedminecraft.network;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public class IntArray {
    //why does this not exist? surely it exists somewhere
    public static final StreamCodec<ByteBuf, int[]> ARRAY_CODEC = new StreamCodec<>() {
        public int @NonNull [] decode(@NonNull ByteBuf byteBuf) {
            int length = VarInt.read(byteBuf);

            int[] array = new int[length];
            for(int j = 0; j < length; j++) {
                array[j] = VarInt.read(byteBuf);
            }
            return array;
        }

        public void encode(@NonNull ByteBuf byteBuf, int[] array) {
            VarInt.write(byteBuf, array.length);

            for (int i : array) {
                VarInt.write(byteBuf, i);
            }
        }
    };

    public static final StreamCodec<ByteBuf, List<Integer>> LIST_CODEC = new StreamCodec<>() {
        public @NonNull List<Integer> decode(@NonNull ByteBuf byteBuf) {
            int length = VarInt.read(byteBuf);

            List<Integer> list = new ArrayList<>(length);
            for(int j = 0; j < length; j++) {
                list.add(VarInt.read(byteBuf));
            }
            return list;
        }

        public void encode(@NonNull ByteBuf byteBuf, List<Integer> list) {
            VarInt.write(byteBuf, list.size());

            for (int i : list) {
                VarInt.write(byteBuf, i);
            }
        }
    };
}
