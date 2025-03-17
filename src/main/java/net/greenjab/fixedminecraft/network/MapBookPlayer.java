package net.greenjab.fixedminecraft.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class MapBookPlayer {
    public String name = "";
    public double x = 0.0;
    public double z = 0.0;
    public float yaw = 0.0f;
    public String dimension = "";


    public void setPlayer(PlayerEntity player) {
        this.name = player.getName().getLiteralString();
        this.x = player.getX();
        this.z = player.getZ();
        this.yaw = player.getYaw();
        this.dimension = player.getWorld().getDimension().toString();
    }

    void toPacket(PacketByteBuf buf) {
        buf.writeString(name);
        buf.writeDouble(x);
        buf.writeDouble(z);
        buf.writeFloat(yaw);
        buf.writeString(dimension);
    }

    static MapBookPlayer fromPacket(PacketByteBuf buf) {
        MapBookPlayer p = new MapBookPlayer();
        p.name = buf.readString();
        p.x = buf.readDouble();
        p.z = buf.readDouble();
        p.yaw = buf.readFloat();
        p.dimension = buf.readString();
        return p;
    }
}
