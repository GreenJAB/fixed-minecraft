package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.function.Consumer;

public record BaitComponent(int level) implements TooltipAppender {
    public static final Codec<BaitComponent> CODEC = Codec.INT.xmap(BaitComponent::new, BaitComponent::level);
    public static final PacketCodec<ByteBuf, BaitComponent> PACKET_CODEC = PacketCodecs.VAR_INT.xmap(BaitComponent::new, BaitComponent::level);

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        textConsumer.accept(Text.translatable("component.fixedminecraft.bait", this.level).formatted(Formatting.GRAY));
    }
}
