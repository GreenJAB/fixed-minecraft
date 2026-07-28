package net.greenjab.fixedminecraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;


public class GameRuleNetwork {
    public static final StreamCodec<FriendlyByteBuf, GameRuleStatus> GAME_RULES = new StreamCodec<>() {
        public @NonNull GameRuleStatus decode(@NonNull FriendlyByteBuf byteBuf) {
            return GameRuleStatus.fromPacket(byteBuf);
        }
        public void encode(@NonNull FriendlyByteBuf byteBuf, GameRuleStatus mapBookPlayer) {
            mapBookPlayer.toPacket(byteBuf);
        }
    };
}
