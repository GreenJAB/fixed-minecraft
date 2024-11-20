package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
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

@Mixin(MapState.class)
public class MapStateMixin {

    @Shadow
    @Final
    private Map<String, MapBannerMarker> banners;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;addIcon(Lnet/minecraft/item/map/MapIcon$Type;Lnet/minecraft/world/WorldAccess;Ljava/lang/String;DDDLnet/minecraft/text/Text;)V", ordinal = 2))
    private void addDecoToBanners(PlayerEntity player, ItemStack stack, CallbackInfo ci, @Local(ordinal = 1) NbtCompound nbt){
        if (this.banners.isEmpty()) {
            BlockPos bp = new BlockPos((int)nbt.getDouble("x"), -32768, (int)nbt.getDouble("z"));
            Text t = Text.of("¶" + nbt.getByte("type"));
            MapBannerMarker mapBannerMarker = new MapBannerMarker(bp, DyeColor.BLACK, t);
            this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
        }
    }

    @ModifyVariable(method = "addIcon", at = @At("STORE"), ordinal = 0)
    private MapIcon injected(MapIcon m, @Local(argsOnly = true) MapIcon.Type type, @Local(ordinal = 0) byte x, @Local(ordinal = 1) byte z, @Local(ordinal = 2) byte rotation, @Local(
            argsOnly = true
    ) Text text) {
        if (text != null) {
            if (Objects.requireNonNull(text.getLiteralString()).charAt(0) == '¶') {
                String[] s = text.getLiteralString().split("¶");
                byte b = (byte) Integer.parseInt(s[1]);

                type = MapIcon.Type.byId(b);
                return new MapIcon(type, x, z, rotation, null);
            }
        }
        return m;
    }

    @Redirect(method = "removeBanner", at = @At(value = "INVOKE",
                                                target = "Lnet/minecraft/item/map/MapBannerMarker;equals(Ljava/lang/Object;)Z"
    ))
    private boolean noRemoveCustomIconBanner(MapBannerMarker instance, Object o){
        if (instance.getPos().getY() == -32768) {
            return true;
        }
        if (instance.getPos().getY() <= -1000) {
            return false;
        }
        return instance.equals(o);
    }



}
