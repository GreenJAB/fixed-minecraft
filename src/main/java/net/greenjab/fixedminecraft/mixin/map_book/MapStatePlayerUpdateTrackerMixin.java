package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.greenjab.fixedminecraft.MapPacketAccessor;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MapState.PlayerUpdateTracker.class)
public class MapStatePlayerUpdateTrackerMixin {
    @Shadow @Final MapState field_132;

    @ModifyReturnValue(at = @At(value = "RETURN", ordinal = 0), method = "getPacket")
    private @Nullable Packet<?> setPosition(@Nullable Packet<?> original) {
        assert original != null;
        ((MapPacketAccessor)original).fixedminecraft$setX(field_132.centerX);
        ((MapPacketAccessor)original).fixedminecraft$setZ(field_132.centerZ);
        return original;
    }
}
