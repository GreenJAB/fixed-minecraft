package net.greenjab.fixedminecraft.network;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SaturationSyncPayload(float saturation) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, SaturationSyncPayload> CODEC = CustomPacketPayload.codec(SaturationSyncPayload::write, SaturationSyncPayload::new);
    public static final CustomPacketPayload.Type<SaturationSyncPayload> ID = new Type<>(FixedMinecraft.id("saturation"));

    public SaturationSyncPayload(FriendlyByteBuf buf)
    {
        this(buf.readFloat());
    }

    public void write(FriendlyByteBuf buf)
    {
        buf.writeFloat(saturation);
    }

    public float getSaturation()
    {
        return saturation;
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type()
    {
        return ID;
    }
}
