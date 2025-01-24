package net.greenjab.fixedminecraft.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ExhaustionSyncPayload(float exhaustion) implements CustomPayload
{
    public static final PacketCodec<PacketByteBuf, ExhaustionSyncPayload> CODEC = CustomPayload.codecOf(ExhaustionSyncPayload::write, ExhaustionSyncPayload::new);
    public static final CustomPayload.Id<ExhaustionSyncPayload> ID = new Id<>(Identifier.of("fixedminecraft", "exhaustion"));

    public ExhaustionSyncPayload(PacketByteBuf buf)
    {
        this(buf.readFloat());
    }

    public void write(PacketByteBuf buf)
    {
        buf.writeFloat(exhaustion);
    }

    public float getExhaustion()
    {
        return exhaustion;
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return ID;
    }
}
