package net.greenjab.fixedminecraft.mixin.map_book;

import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapDecoration.class)
public abstract class MapDecorationMixin {

    @Redirect(method = "isAlwaysRendered", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapDecorationType;showOnItemFrame()Z"))
    private boolean notInfEffect(MapDecorationType instance){
        return false;
    }

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 15))
    private int letPlayerMarkerScale(int constant) {
        return 255;
    }

}
