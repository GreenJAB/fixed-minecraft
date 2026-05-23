package net.greenjab.fixedminecraft.mixin.other;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.DyedItemColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DyedItemColor.class)
public abstract class DyedItemColorMixin {
    @Redirect(method = "applyDyes(Lnet/minecraft/world/item/component/DyedItemColor;Ljava/util/List;)Lnet/minecraft/world/item/component/DyedItemColor;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColor()I"))
    private static int brighterColours2(DyeColor instance) {
        return instance.getTextColor();
    }
}
