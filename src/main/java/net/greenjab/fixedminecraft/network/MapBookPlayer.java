package net.greenjab.fixedminecraft.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class MapBookPlayer {
    public String name = "";
    public double x = 0.0;
    public double y = 0.0;
    public double z = 0.0;
    public float yaw = 0.0f;
    public String dimension = "";

    public static final Codec<MapBookPlayer> CODEC = RecordCodecBuilder.create(
           instance -> instance.group(
                            Codec.STRING.fieldOf("name").forGetter(mapPlayer -> mapPlayer.name),
                            Codec.DOUBLE.fieldOf("x").forGetter(mapPlayer -> mapPlayer.x),
                            Codec.DOUBLE.fieldOf("y").forGetter(mapPlayer -> mapPlayer.y),
                            Codec.DOUBLE.fieldOf("z").forGetter(mapPlayer -> mapPlayer.z),
                            Codec.FLOAT.fieldOf("yaw").forGetter(mapPlayer -> mapPlayer.yaw),
                            Codec.STRING.fieldOf("dimension").forGetter(mapPlayer -> mapPlayer.dimension)
                    )
                    .apply(instance, MapBookPlayer::new)
    );

    public MapBookPlayer(){
    }

    public MapBookPlayer(String name, double x, double y, double z, float yaw, String dimension){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.dimension = dimension;
    }

    public void setPlayer(PlayerEntity player) {
        this.name = player.getName().getLiteralString();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.yaw = player.getYaw();
        this.dimension = player.getWorld().getDimensionEntry().getIdAsString();
    }

    void toPacket(PacketByteBuf buf) {
        buf.writeString(name);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(yaw);
        buf.writeString(dimension);
    }

    static MapBookPlayer fromPacket(PacketByteBuf buf) {
        MapBookPlayer p = new MapBookPlayer();
        p.name = buf.readString();
        p.x = buf.readDouble();
        p.y = buf.readDouble();
        p.z = buf.readDouble();
        p.yaw = buf.readFloat();
        p.dimension = buf.readString();
        return p;
    }
}
