package net.greenjab.fixedminecraft.registry.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public class MapDecorationRegistry {

    public static Holder<MapDecorationType> PILLAGER_OUTPOST = register("outpost", "outpost", MapColor.COLOR_BROWN.col);
    public static Holder<MapDecorationType> RUINED_PORTAL = register("ruined_portal", "ruined_portal", MapColor.COLOR_PURPLE.col);
    public static Holder<MapDecorationType> TRAIL_RUINS = register("trail_ruins", "trail_ruins", MapColor.COLOR_LIGHT_GRAY.col);

    private static Holder<MapDecorationType> register(
            String id, String assetId, int mapColor
    ) {
        ResourceKey<MapDecorationType> registryKey = ResourceKey.create(Registries.MAP_DECORATION_TYPE, Identifier.withDefaultNamespace(id));
        MapDecorationType mapDecorationType = new MapDecorationType(Identifier.withDefaultNamespace(assetId), true, mapColor, true, false);
        return Registry.registerForHolder(BuiltInRegistries.MAP_DECORATION_TYPE, registryKey, mapDecorationType);
    }

    public static void registerMapDecorations() {
        System.out.println("register MapDecorations");
    }
}
