package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(MapBannerMarker.class)
public class MapBannerMarkerMixin {

    @Inject(method = "fromWorldBlock", at = @At("HEAD"),cancellable = true)
    private static void fakeBanner(BlockView blockView, BlockPos blockPos, CallbackInfoReturnable<MapBannerMarker> cir){
        if (blockPos.getY()<=-1000) {
            DyeColor[] dye = {DyeColor.PURPLE, DyeColor.PINK, DyeColor.LIGHT_BLUE, DyeColor.RED};
            cir.setReturnValue(new MapBannerMarker(blockPos, dye[Math.abs(blockPos.getY())-1000], null));
        }
    }
}
