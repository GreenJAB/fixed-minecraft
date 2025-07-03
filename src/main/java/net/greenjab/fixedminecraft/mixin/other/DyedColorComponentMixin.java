package net.greenjab.fixedminecraft.mixin.other;

import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DyedColorComponent.class)
public class DyedColorComponentMixin {
    @Redirect(method = "setColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/DyeColor;getEntityColor()I"))
    private static int brighterColours(DyeColor instance) {
        return instance.getSignColor();
    }
}
