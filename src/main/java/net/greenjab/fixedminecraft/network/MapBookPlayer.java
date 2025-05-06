package net.greenjab.fixedminecraft.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;


public class MapBookPlayer {
    public String name = "";
    public double x = 0.0;
    public double z = 0.0;
    public float yaw = 0.0f;
    public String dimension = "";

    public static final Codec<MapBookPlayer> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.STRING.fieldOf("name").forGetter(mapPlayer -> mapPlayer.name),
                            Codec.DOUBLE.fieldOf("x").forGetter(mapPlayer -> mapPlayer.x),
                            Codec.DOUBLE.fieldOf("z").forGetter(mapPlayer -> mapPlayer.z),
                            Codec.FLOAT.fieldOf("yaw").forGetter(mapPlayer -> mapPlayer.yaw),
                            Codec.STRING.fieldOf("dimension").forGetter(mapPlayer -> mapPlayer.dimension)
                    )
                    .apply(instance, MapBookPlayer::new)
    );

    public MapBookPlayer(){
    }

    public MapBookPlayer(String name, double x, double z, float yaw, String dimension){
        this.name = name;
        this.x = x;
        this.z = z;
        this.yaw = yaw;
        this.dimension = dimension;
    }

    public void setPlayer(PlayerEntity player) {
        this.name = player.getName().getLiteralString();
        this.x = player.getX();
        this.z = player.getZ();
        this.yaw = player.getYaw();
        this.dimension = player.getWorld().getDimensionEntry().getIdAsString();
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

    //TODO
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("MBPname", this.name);
        nbt.putDouble("MBPx", this.x);
        nbt.putDouble("MBPz", this.z);
        nbt.putFloat("MBPyaw", this.yaw);
        nbt.putString("MBPdimension", this.dimension);
    }

    public static MapBookPlayer fromNbt(NbtCompound nbt) {
        MapBookPlayer p = new MapBookPlayer();
        p.name = nbt.getString("MBPname").get();
        p.x = nbt.getDouble("MBPx").get();
        p.z = nbt.getDouble("MBPz").get();
        p.yaw = nbt.getFloat("MBPyaw").get();
        p.dimension = nbt.getString("MBPdimension").get();
        return p;
    }

    public String toString() {
        return "" + this.name + ", " + this.x + ", " + this.z + ", " + this.dimension;
    }
}
