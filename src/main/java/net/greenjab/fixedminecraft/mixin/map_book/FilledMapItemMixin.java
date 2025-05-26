package net.greenjab.fixedminecraft.mixin.map_book;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.FilledMapItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FilledMapItem.class)
public class FilledMapItemMixin  {

    @ModifyExpressionValue(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/DimensionType;hasCeiling()Z"))
    private boolean updateNetherColours(boolean original) {
        return false;
    }

}
