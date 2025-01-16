package net.greenjab.fixedminecraft.mixin.map_book;

import net.greenjab.fixedminecraft.MapPacketAccessor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapUpdateS2CPacket.class)
public class MapUpdateS2CPacketMixin implements MapPacketAccessor {
    @Unique
    private int x;
    @Unique
    private int z;

    /*@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readByte()B", ordinal = 0), method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V")
    private void read(PacketByteBuf buf, CallbackInfo ci) {
        x = buf.readInt();
        z = buf.readInt();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeByte(I)Lnet/minecraft/network/PacketByteBuf;", ordinal = 0), method = "write")
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeInt(x);
        buf.writeInt(z);
    }*/

    @Override
    public void fixedminecraft$setX(int x) {
        this.x = x;
    }

    @Override
    public void fixedminecraft$setZ(int z) {
        this.z = z;
    }

    @Override
    public int fixedminecraft$readX() {
        return x;
    }

    @Override
    public int fixedminecraft$readZ() {
        return z;
    }
}
