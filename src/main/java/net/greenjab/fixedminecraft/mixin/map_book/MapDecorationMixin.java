package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MapDecoration.class)
public abstract class MapDecorationMixin {

    @WrapOperation(method = "renderOnFrame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/saveddata/maps/MapDecorationType;showOnItemFrame()Z"))
    private boolean notInfEffect(MapDecorationType instance, Operation<Boolean> original){
        return false;
    }

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 15))
    private int letPlayerMarkerScale(int constant) {
        return 255;
    }

}
