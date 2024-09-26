package net.greenjab.fixedminecraft.mixin.map_book;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.item.map.MapIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapIcon.class)
public abstract class MapIconMixin {

    @Redirect(method = "isAlwaysRendered", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapIcon$Type;isAlwaysRendered()Z"))
    private boolean notInfEffect(MapIcon.Type instance){
        return false;
    }

}
