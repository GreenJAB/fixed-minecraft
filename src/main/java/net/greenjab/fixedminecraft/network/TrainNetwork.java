package net.greenjab.fixedminecraft.network;

import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public class TrainNetwork {
    public static final StreamCodec<FriendlyByteBuf, ArrayList<UUID>> ARRAY_CODEC = new StreamCodec<>() {
        public @NonNull ArrayList<UUID> decode(@NonNull FriendlyByteBuf byteBuf) {
            int length = VarInt.read(byteBuf);
            ArrayList<UUID> array = new ArrayList<>();
            for(int j = 0; j < length; j++) {
                array.add(byteBuf.readUUID());
            }
            return array;
        }

        public void encode(@NonNull FriendlyByteBuf byteBuf, ArrayList<UUID> array) {
            ArrayList<UUID> array2 = (ArrayList<UUID>) array.clone();
            VarInt.write(byteBuf, array.size());
            for (int i = 0; i < array.size();i++) {
                byteBuf.writeUUID(array2.get(i));
            }
        }
    };
}

