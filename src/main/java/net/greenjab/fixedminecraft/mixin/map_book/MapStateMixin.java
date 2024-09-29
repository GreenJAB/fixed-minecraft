package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapBannerMarker;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MapState.class)
public class MapStateMixin {

    @Shadow
    @Final
    private Map<String, MapBannerMarker> banners;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;addIcon(Lnet/minecraft/item/map/MapIcon$Type;Lnet/minecraft/world/WorldAccess;Ljava/lang/String;DDDLnet/minecraft/text/Text;)V", ordinal = 2))
    private void addDeco(PlayerEntity player, ItemStack stack, CallbackInfo ci, @Local(ordinal = 1) NbtCompound nbt){
        if (this.banners.size() == 0) {
            BlockPos bp = new BlockPos((int)nbt.getDouble("x"), -32768, (int)nbt.getDouble("z"));
            Text t = Text.of("¶" + nbt.getByte("type"));
            MapBannerMarker mapBannerMarker = new MapBannerMarker(bp, DyeColor.BLACK, t);
            this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
        }
    }

    @Redirect(method = "removeBanner", at = @At(value = "INVOKE",
                                                target = "Lnet/minecraft/item/map/MapBannerMarker;equals(Ljava/lang/Object;)Z"
    ))
    private boolean noRemoveCustomIconBanner(MapBannerMarker instance, Object o){
        if (instance.getPos().getY() == -32768) {
            return true;
        }
        return instance.equals(o);
    }

}
