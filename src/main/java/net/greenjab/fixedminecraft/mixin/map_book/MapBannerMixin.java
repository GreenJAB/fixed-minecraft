package net.greenjab.fixedminecraft.mixin.map_book;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.saveddata.maps.MapBanner;

@Mixin(MapBanner.class)
public abstract class MapBannerMixin {

    @Inject(method = "fromWorld", at = @At("HEAD"),cancellable = true)
    private static void fakeBanner(BlockGetter level, BlockPos pos, CallbackInfoReturnable<MapBanner> cir){
        if (pos.getY() <= -1000 && pos.getY() > -2000) {
            DyeColor[] dye = {DyeColor.PURPLE, DyeColor.PINK, DyeColor.LIGHT_BLUE, DyeColor.RED, DyeColor.CYAN, DyeColor.LIGHT_GRAY};
            cir.setReturnValue(new MapBanner(pos, dye[Math.abs(pos.getY()) - 1000], Optional.empty()));
        }
    }
}
