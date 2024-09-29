package net.greenjab.fixedminecraft.mixin.map_book;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.item.map.MapIcon;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapIcon.class)
public abstract class MapIconMixin {

    @Shadow
    @Final
    private @Nullable Text text;

    @Redirect(method = "isAlwaysRendered", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapIcon$Type;isAlwaysRendered()Z"))
    private boolean notInfEffect(MapIcon.Type instance){
        if (this.text != null) {
            if (this.text.getLiteralString().charAt(0) == '§'){
                return false;
            }
        }
        return false;
        //return true;
    }

}
