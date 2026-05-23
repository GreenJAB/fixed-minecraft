package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;

public record BaitComponent(int level) implements TooltipProvider {
    public static final Codec<BaitComponent> CODEC = Codec.INT.xmap(BaitComponent::new, BaitComponent::level);
    public static final StreamCodec<ByteBuf, BaitComponent> PACKET_CODEC = ByteBufCodecs.VAR_INT.map(BaitComponent::new, BaitComponent::level);

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext context, Consumer<Component> textConsumer, @NonNull TooltipFlag type, @NonNull DataComponentGetter components) {
        textConsumer.accept(Component.translatable("component.fixedminecraft.bait", this.level).withStyle(ChatFormatting.GRAY));
    }
}
