package net.greenjab.fixedminecraft.mixin.map_book;

import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapDecoration.class)
public abstract class MapDecorationMixin {

    @Redirect(method = "isAlwaysRendered", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapDecorationType;showOnItemFrame()Z"))
    private boolean notInfEffect(MapDecorationType instance){
        return false;
    }

}
