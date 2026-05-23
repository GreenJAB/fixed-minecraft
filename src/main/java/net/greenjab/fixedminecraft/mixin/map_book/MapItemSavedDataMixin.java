package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateAccessor;
import net.greenjab.fixedminecraft.registry.registries.MapDecorationRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Mixin(MapItemSavedData.class)
public abstract class MapItemSavedDataMixin implements MapStateAccessor {

    @Shadow
    @Final
    private Map<String, MapBanner> bannerMarkers;

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

    @Unique
    private static HashMap<Holder<MapDecorationType>, Integer> decoToColor;

    static {
        decoToColor = new HashMap<>();
        decoToColor.put(MapDecorationTypes.PLAINS_VILLAGE, 3003659);
        decoToColor.put(MapDecorationTypes.DESERT_VILLAGE, 16766219);
        decoToColor.put(MapDecorationTypes.SAVANNA_VILLAGE, 13536268);
        decoToColor.put(MapDecorationTypes.TAIGA_VILLAGE, 6857828);
        decoToColor.put(MapDecorationTypes.SNOWY_VILLAGE, 14872575);
        decoToColor.put(MapDecorationTypes.JUNGLE_TEMPLE, 1999367);
        decoToColor.put(MapDecorationTypes.SWAMP_HUT, 5390853);

        decoToColor.put(MapDecorationRegistry.PILLAGER_OUTPOST, 10373376);
        decoToColor.put(MapDecorationRegistry.RUINED_PORTAL, 11796480);
    }

    @Inject(method = "addTargetDecoration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/saveddata/maps/MapDecorationType;hasMapColor()Z"), cancellable = true)
    private static void addColorToBlandMaps(ItemStack itemStack, BlockPos position, String key, Holder<MapDecorationType> decorationType,
                                            CallbackInfo ci){
        if (decoToColor.containsKey(decorationType)){
            itemStack.set(DataComponents.MAP_COLOR, new MapItemColor(decoToColor.get(decorationType)));
            ci.cancel();
        }
    }

    @Inject(method = "addDecoration", at = @At(value = "TAIL"))
    private void addDecoToBanners(Holder<MapDecorationType> type, @Nullable LevelAccessor level, String key, double xPos, double zPos,
                                  double yRot, @Nullable Component name, CallbackInfo ci){
        if (type.value().explorationMapElement()) {
            if (this.bannerMarkers.isEmpty()) {
                BlockPos bp = new BlockPos((int) xPos, -32768, (int) zPos);
                Optional<Component> t = Optional.of(Component.nullToEmpty("¶" + type.value().assetId()));
                MapBanner mapBannerMarker = new MapBanner(bp, DyeColor.BLACK, t);
                this.bannerMarkers.put(mapBannerMarker.getId(), mapBannerMarker);
            }
        }
    }

    @Redirect(method = "addDecoration", at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/core/Holder;BBBLjava/util/Optional;)Lnet/minecraft/world/level/saveddata/maps/MapDecoration;"
    ))
    private MapDecoration mapTypeAsCustomName(Holder<MapDecorationType> registryEntry, byte x, byte z, byte rot, Optional<Component> optional,
                                              @Local(argsOnly = true) Holder<MapDecorationType> type,
                                              @Local(argsOnly = true) Component name) {
        if (name != null) {
            if (Objects.requireNonNull(name.tryCollapseToString()).charAt(0) == '¶') {
                String[] s = name.tryCollapseToString().split("¶");
                type = getMapType(s[1]);
                return new MapDecoration(type, x, z, rot, Optional.empty());
            }

            if (Objects.requireNonNull(name.tryCollapseToString()).charAt(0) == '[') {
                String[] s = name.tryCollapseToString().split("\\[");
                if (s.length == 2) {
                    String[] s2 = s[1].split("]");
                    Holder<MapDecorationType> type2 = getMapTypeLimited(s2[0]);
                    if (type2 != null) {
                        if (s2.length == 1) {
                            return new MapDecoration(type2, x, z, rot, Optional.empty());
                        } else if (s2.length == 2) {
                            if (s2[1].charAt(0) == ' ') s2[1] = s2[1].substring(1);
                            return new MapDecoration(type2, x, z, rot, Optional.of(Component.nullToEmpty(s2[1])));
                        }
                    }
                }
            }
        }
        return new MapDecoration(type, x, z, rot, optional);
    }



    @Redirect(method = "checkBanners", at = @At(value = "INVOKE",
                                                target = "Lnet/minecraft/world/level/saveddata/maps/MapBanner;equals(Ljava/lang/Object;)Z"
    ))
    private boolean noRemoveCustomIconBanner(MapBanner instance, Object o){
        if (instance.pos().getY() == -32768) {
            return true;
        }
        if (instance.pos().getY() <= -1000) {
            return false;
        }
        return instance.equals(o);
    }

    @Inject(method = "playerDecorationTypeAndRotation", at = @At("HEAD"), cancellable = true)
    private void betterPlayerMarker(Holder<MapDecorationType> type, @Nullable LevelAccessor level, double yRot, float xDeltaFromCenter, float yDeltaFromCenter,
                                    CallbackInfoReturnable<Pair<Holder<MapDecorationType>, Byte>> cir) {
        double rot = yRot < 0.0 ? yRot + 360.0 : yRot;
        rot = (rot + 8) * 16.0 / 360.0;
        if (rot >= 16) rot = 0;

        //blocksPerScale * 4 bit - distance from center of map
        double scale = (64 * 16) - (Math.abs(xDeltaFromCenter) + Math.abs(yDeltaFromCenter));
        scale /= 64;
        scale = Math.max(Math.floor(scale), this.unlimitedTracking ? 1 : 0);
        scale = Math.min(scale, 14);

        //flip end values so everything else renders fine
        if (scale == 0) scale = 15;
        if (isInBounds(xDeltaFromCenter, yDeltaFromCenter)) scale = 0;
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
            "swamp_hut", "trial_chambers", "outpost"};

    @Inject(method = "getBanners", at = @At(value = "RETURN"), cancellable = true)
    private void updateName(CallbackInfoReturnable<Collection<MapBanner>> cir) {
        Collection<MapBanner> banners = cir.getReturnValue();
        ArrayList<MapBanner> newBanners = new ArrayList<>();
        for (MapBanner banner : banners) {
            Optional<Component> original = banner.name();
            if (original.isPresent()) {
                String name = original.get().getString();
                if (name.contains("¶")) {
                    try {
                        int i = Integer.parseInt(name.substring(1));
                        newBanners.add(new MapBanner(banner.pos(), banner.color(), Optional.of(Component.nullToEmpty("¶"+updateNames[i]))));
                    } catch (NumberFormatException e) {
                        newBanners.add(banner);
                    }
                }
            }
        }
        cir.setReturnValue(newBanners);
    }

    @Unique
    private Holder<MapDecorationType> getMapType(String type) {
        if (type.contains("woodland_mansion")) return MapDecorationTypes.WOODLAND_MANSION;
        if (type.contains("ocean_monument")) return MapDecorationTypes.OCEAN_MONUMENT;
        if (type.contains("desert_village")) return MapDecorationTypes.DESERT_VILLAGE;
        if (type.contains("plains_village")) return MapDecorationTypes.PLAINS_VILLAGE;
        if (type.contains("savanna_village")) return MapDecorationTypes.SAVANNA_VILLAGE;
        if (type.contains("snowy_village")) return MapDecorationTypes.SNOWY_VILLAGE;
        if (type.contains("taiga_village")) return MapDecorationTypes.TAIGA_VILLAGE;
        if (type.contains("jungle_temple")) return MapDecorationTypes.JUNGLE_TEMPLE;
        if (type.contains("swamp_hut")) return MapDecorationTypes.SWAMP_HUT;
        if (type.contains("trial_chambers")) return MapDecorationTypes.TRIAL_CHAMBERS;
        if (type.contains("red_x")) return MapDecorationTypes.RED_X;

        if (type.contains("white_banner")) return MapDecorationTypes.WHITE_BANNER;
        if (type.contains("orange_banner")) return MapDecorationTypes.ORANGE_BANNER;
        if (type.contains("magenta_banner")) return MapDecorationTypes.MAGENTA_BANNER;
        if (type.contains("light_blue_banner")) return MapDecorationTypes.LIGHT_BLUE_BANNER;
        if (type.contains("yellow_banner")) return MapDecorationTypes.YELLOW_BANNER;
        if (type.contains("lime_banner")) return MapDecorationTypes.LIME_BANNER;
        if (type.contains("pink_banner")) return MapDecorationTypes.PINK_BANNER;
        if (type.contains("gray_banner")) return MapDecorationTypes.GRAY_BANNER;
        if (type.contains("light_gray_banner")) return MapDecorationTypes.LIGHT_GRAY_BANNER;
        if (type.contains("cyan_banner")) return MapDecorationTypes.CYAN_BANNER;
        if (type.contains("purple_banner")) return MapDecorationTypes.PURPLE_BANNER;
        if (type.contains("blue_banner")) return MapDecorationTypes.BLUE_BANNER;
        if (type.contains("brown_banner")) return MapDecorationTypes.BROWN_BANNER;
        if (type.contains("green_banner")) return MapDecorationTypes.GREEN_BANNER;
        if (type.contains("red_banner")) return MapDecorationTypes.RED_BANNER;

        if (type.contains("player")) return MapDecorationTypes.PLAYER;
        if (type.contains("frame")) return MapDecorationTypes.FRAME;
        if (type.contains("red_marker")) return MapDecorationTypes.RED_MARKER;
        if (type.contains("blue_marker")) return MapDecorationTypes.BLUE_MARKER;
        if (type.contains("target_x")) return MapDecorationTypes.TARGET_X;
        if (type.contains("target_point")) return MapDecorationTypes.TARGET_POINT;
        if (type.contains("player_off_map")) return MapDecorationTypes.PLAYER_OFF_MAP;
        if (type.contains("player_off_limits")) return MapDecorationTypes.PLAYER_OFF_LIMITS;

        if (type.contains("outpost")) return MapDecorationRegistry.PILLAGER_OUTPOST;
        if (type.contains("portal")) return MapDecorationRegistry.RUINED_PORTAL;
        if (type.contains("trail_ruins")) return MapDecorationRegistry.TRAIL_RUINS;

        return MapDecorationTypes.BLACK_BANNER;
    }

    @Unique
    private Holder<MapDecorationType> getMapTypeLimited(String type) {
        if (type.contains("woodland_mansion")) return MapDecorationTypes.WOODLAND_MANSION;
        if (type.contains("ocean_monument")) return MapDecorationTypes.OCEAN_MONUMENT;
        if (type.contains("desert_village")) return MapDecorationTypes.DESERT_VILLAGE;
        if (type.contains("plains_village")) return MapDecorationTypes.PLAINS_VILLAGE;
        if (type.contains("savanna_village")) return MapDecorationTypes.SAVANNA_VILLAGE;
        if (type.contains("snowy_village")) return MapDecorationTypes.SNOWY_VILLAGE;
        if (type.contains("taiga_village")) return MapDecorationTypes.TAIGA_VILLAGE;
        if (type.contains("jungle_temple")) return MapDecorationTypes.JUNGLE_TEMPLE;
        if (type.contains("swamp_hut")) return MapDecorationTypes.SWAMP_HUT;
        if (type.contains("trial_chambers")) return MapDecorationTypes.TRIAL_CHAMBERS;
        if (type.contains("red_x")) return MapDecorationTypes.RED_X;
        if (type.contains("target_point")) return MapDecorationTypes.TARGET_POINT;
        if (type.contains("outpost")) return MapDecorationRegistry.PILLAGER_OUTPOST;
        if (type.contains("portal")) return MapDecorationRegistry.RUINED_PORTAL;
        if (type.contains("trail_ruins")) return MapDecorationRegistry.TRAIL_RUINS;

        return null;
    }
}
