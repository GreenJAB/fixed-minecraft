package net.greenjab.fixedminecraft.mixin.map_book;

import net.minecraft.item.map.MapState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MapState.class)
public interface MapStateAccessor {
    @Invoker("<init>")
    static MapState createMapState(int centerX, int centerZ, byte scale, boolean showIcons, boolean unlimitedTracking, boolean locked, RegistryKey<World> dimension) {
        throw new UnsupportedOperationException();
    }
}
