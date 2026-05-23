package net.greenjab.fixedminecraft.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

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
                            Codec.DOUBLE.optionalFieldOf("y").forGetter(mapPlayer -> Optional.of(mapPlayer.y)),
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

    public MapBookPlayer(String name, Double x, Optional<Double> y, Double z, Float yaw, String dimension) {
        this(name, x, y.orElse(0.0), z, yaw, dimension);
    }

    public void setPlayer(Player player) {
        this.name = player.getName().tryCollapseToString();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.yaw = player.getYRot();
        this.dimension = player.level().dimension().identifier().toString();
    }

    void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(yaw);
        buf.writeUtf(dimension);
    }

    static MapBookPlayer fromPacket(FriendlyByteBuf buf) {
        MapBookPlayer p = new MapBookPlayer();
        p.name = buf.readUtf();
        p.x = buf.readDouble();
        p.y = buf.readDouble();
        p.z = buf.readDouble();
        p.yaw = buf.readFloat();
        p.dimension = buf.readUtf();
        return p;
    }
}
