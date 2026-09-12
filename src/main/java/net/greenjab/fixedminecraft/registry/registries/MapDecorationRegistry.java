package net.greenjab.fixedminecraft.registry.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public class MapDecorationRegistry {

    public static Holder<MapDecorationType> PILLAGER_OUTPOST = register("outpost", "outpost");
    public static Holder<MapDecorationType> RUINED_PORTAL = register("ruined_portal", "ruined_portal");
    public static Holder<MapDecorationType> TRAIL_RUINS = register("trail_ruins", "trail_ruins");

    private static Holder<MapDecorationType> register(
            String id, String assetId
    ) {
        ResourceKey<MapDecorationType> registryKey = ResourceKey.create(Registries.MAP_DECORATION_TYPE, Identifier.withDefaultNamespace(id));
        MapDecorationType mapDecorationType = new MapDecorationType(Identifier.withDefaultNamespace(assetId), true, false);
        return Registry.registerForHolder(BuiltInRegistries.MAP_DECORATION_TYPE, registryKey, mapDecorationType);
    }

    public static void registerMapDecorations() {
        System.out.println("register MapDecorations");
    }
}
