package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Mixin(MapState.class)
public class MapStateMixin {

    @Shadow
    @Final
    private Map<String, MapBannerMarker> banners;

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

    @ModifyVariable(method = "addDecoration", at = @At("STORE"), ordinal = 0)
    private MapDecoration injected(MapDecoration m, @Local(argsOnly = true) RegistryEntry<MapDecorationType> type,
                                   @Local(ordinal = 0, argsOnly = true) double x, @Local(ordinal = 1, argsOnly = true) double z,
                                   @Local(ordinal = 2, argsOnly = true) double rotation, @Local(
            argsOnly = true
    ) Text text) {
        if (text != null) {
            if (Objects.requireNonNull(text.getLiteralString()).charAt(0) == '¶') {
                String[] s = text.getLiteralString().split("¶");
                byte b = (byte) Integer.parseInt(s[1]);

                type = MapDecorationTypes.BANNER_BLACK;
                return new MapDecoration(type, (byte) x, (byte) z, (byte)rotation, null);
            }
        }
        return m;
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



}
