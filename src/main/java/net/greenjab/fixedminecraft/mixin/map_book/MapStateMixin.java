package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateAccessor;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Mixin(MapState.class)
public class MapStateMixin implements MapStateAccessor {

    @Shadow
    @Final
    private Map<String, MapBannerMarker> banners;

    @Final @Shadow @Mutable
    public int centerX;

    @Final @Shadow @Mutable
    public int centerZ;

    @Shadow
    @Final
    private boolean unlimitedTracking;

    @Override
    public void fixedminecraft$setPosition(int centerX, int centerZ) {
        this.centerX = centerX;
        this.centerZ = centerZ;
    }

    @Inject(method = "addDecoration", at = @At(value = "TAIL"))
    private void addDecoToBanners(RegistryEntry<MapDecorationType> type, @Nullable WorldAccess world, String key, double x, double z,
                                  double rotation, @Nullable Text text, CallbackInfo ci){
        if (type.value().explorationMapElement()) {
            if (this.banners.isEmpty()) {
                BlockPos bp = new BlockPos((int) x, -32768, (int) z);
                Optional<Text> t = Optional.of(Text.of("¶" + type.value().assetId()));
                MapBannerMarker mapBannerMarker = new MapBannerMarker(bp, DyeColor.BLACK, t);
                this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
            }
        }
    }

    @Redirect(method = "addDecoration", at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/registry/entry/RegistryEntry;BBBLjava/util/Optional;)Lnet/minecraft/item/map/MapDecoration;"
    ))
    private MapDecoration mapTypeAsCustomName(RegistryEntry<MapDecorationType> registryEntry, byte x, byte z, byte rotation, Optional<Text> optional,
                                   @Local(argsOnly = true) RegistryEntry<MapDecorationType> type,
                                   @Local(argsOnly = true) Text text) {
        if (text != null) {
            if (Objects.requireNonNull(text.getLiteralString()).charAt(0) == '¶') {
                String[] s = text.getLiteralString().split("¶");
                type = getMapType(s[1]);
                //return new MapDecoration(type, x, z, rotation, optional);
                return new MapDecoration(type, x, z, rotation, Optional.empty());
            }
        }
        return new MapDecoration(type, x, z, rotation, optional);
    }

    @Unique
    private RegistryEntry<MapDecorationType> getMapType(String type) {
        if (type.contains("woodland_mansion")) return MapDecorationTypes.MANSION;
        if (type.contains("ocean_monument")) return MapDecorationTypes.MONUMENT;
        if (type.contains("desert_village")) return MapDecorationTypes.VILLAGE_DESERT;
        if (type.contains("plains_village")) return MapDecorationTypes.VILLAGE_PLAINS;
        if (type.contains("savanna_village")) return MapDecorationTypes.VILLAGE_SAVANNA;
        if (type.contains("snowy_village")) return MapDecorationTypes.VILLAGE_SNOWY;
        if (type.contains("taiga_village")) return MapDecorationTypes.VILLAGE_TAIGA;
        if (type.contains("jungle_temple")) return MapDecorationTypes.JUNGLE_TEMPLE;
        if (type.contains("swamp_hut")) return MapDecorationTypes.SWAMP_HUT;
        if (type.contains("trial_chambers")) return MapDecorationTypes.TRIAL_CHAMBERS;
        if (type.contains("red_x")) return MapDecorationTypes.RED_X;

        if (type.contains("white_banner")) return MapDecorationTypes.BANNER_WHITE;
        if (type.contains("orange_banner")) return MapDecorationTypes.BANNER_ORANGE;
        if (type.contains("magenta_banner")) return MapDecorationTypes.BANNER_MAGENTA;
        if (type.contains("light_blue_banner")) return MapDecorationTypes.BANNER_LIGHT_BLUE;
        if (type.contains("yellow_banner")) return MapDecorationTypes.BANNER_YELLOW;
        if (type.contains("lime_banner")) return MapDecorationTypes.BANNER_LIME;
        if (type.contains("pink_banner")) return MapDecorationTypes.BANNER_PINK;
        if (type.contains("gray_banner")) return MapDecorationTypes.BANNER_GRAY;
        if (type.contains("light_gray_banner")) return MapDecorationTypes.BANNER_LIGHT_GRAY;
        if (type.contains("cyan_banner")) return MapDecorationTypes.BANNER_CYAN;
        if (type.contains("purple_banner")) return MapDecorationTypes.BANNER_PURPLE;
        if (type.contains("blue_banner")) return MapDecorationTypes.BANNER_BLUE;
        if (type.contains("brown_banner")) return MapDecorationTypes.BANNER_BROWN;
        if (type.contains("green_banner")) return MapDecorationTypes.BANNER_GREEN;
        if (type.contains("red_banner")) return MapDecorationTypes.BANNER_RED;
        return MapDecorationTypes.BANNER_BLACK;
    }

    @Redirect(method = "removeBanner", at = @At(value = "INVOKE",
                                                target = "Lnet/minecraft/item/map/MapBannerMarker;equals(Ljava/lang/Object;)Z"
    ))
    private boolean noRemoveCustomIconBanner(MapBannerMarker instance, Object o){
        if (instance.pos().getY() == -32768) {
            return true;
        }
        if (instance.pos().getY() <= -1000) {
            return false;
        }
        return instance.equals(o);
    }

    @Inject(method = "getPlayerMarkerAndRotation", at = @At("HEAD"), cancellable = true)
    private void betterPlayerMarker(RegistryEntry<MapDecorationType> type, @Nullable WorldAccess world, double rotation, float dx, float dz,
                                    CallbackInfoReturnable<Pair<RegistryEntry<MapDecorationType>, Byte>> cir) {
        double rot = rotation < 0.0 ? rotation + 360.0 : rotation;
        rot = (rot + 8) * 16.0 / 360.0;
        if (rot >= 16) rot = 0;

        //blocksPerScale * 4 bit - distance from center of map
        double scale = (64 * 16) - (Math.abs(dx) + Math.abs(dz));
        scale /= 64;
        scale = Math.max(Math.floor(scale), this.unlimitedTracking ? 1 : 0);
        scale = Math.min(scale, 14);

        //flip end values so everything else renders fine
        if (scale == 0) scale = 15;
        if (isInBounds(dx, dz)) scale = 0;
        scale *= 16;

        byte b = (byte) ((int) rot + (byte) ((int) scale));
        cir.setReturnValue(Pair.of(MapDecorationTypes.PLAYER, b));
        cir.cancel();
    }

    @Unique
    private static boolean isInBounds(float dx, float dz) {
        return dx >= -63.0F && dz >= -63.0F && dx <= 63.0F && dz <= 63.0F;
    }

    @Unique
    private static final String[] updateNames = {"player", "frame", "red_marker", "blue_marker", "target_x", "target_point",
            "player_off_map", "player_off_limits", "woodland_mansion", "ocean_monument", "white_banner", "orange_banner", "magenta_banner",
            "light_blue_banner", "light_blue_banner", "lime_banner", "pink_banner", "gray_banner", "light_gray_banner",
            "cyan_banner", "purple_banner", "blue_banner", "brown_banner", "green_banner", "red_banner", "black_banner",
            "red_x", "desert_village", "plains_village", "savanna_village", "snowy_village", "taiga_village", "jungle_temple",
            "swamp_hut", "trial_chambers"};

    @Inject(method = "getBanners", at = @At(value = "RETURN"), cancellable = true)
    private void updateName(CallbackInfoReturnable<Collection<MapBannerMarker>> cir) {
        Collection<MapBannerMarker> banners = cir.getReturnValue();
        ArrayList<MapBannerMarker> newBanners = new ArrayList<MapBannerMarker>();
        for (MapBannerMarker banner : banners) {
            Optional<Text> original = banner.name();
            if (original.isPresent()) {
                String name = original.get().getString();
                if (name.contains("¶")) {
                    try {
                        int i = Integer.parseInt(name.substring(1));
                        newBanners.add(new MapBannerMarker(banner.pos(), banner.color(), Optional.of(Text.of("¶"+updateNames[i]))));
                    } catch (NumberFormatException e) {
                        newBanners.add(banner);
                    }
                }
            }
        }
        cir.setReturnValue(newBanners);
    }
}
