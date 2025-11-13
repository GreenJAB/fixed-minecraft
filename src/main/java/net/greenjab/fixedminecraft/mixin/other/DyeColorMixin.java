package net.greenjab.fixedminecraft.mixin.other;

import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DyeColor.class)
public class DyeColorMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper;fullAlpha(I)I", ordinal = 0))
    private int brighterColours(int argb) {
        return switch (argb) {
            case (10141901) -> 65535; //lightblue
            case (65535) -> 1352117; //cyan
            case (12582656) -> 65280; //lime
            case (65280) -> 2003200; //green
            case (16738335) -> 16744234; //orange
            case (16738740) -> 16739529; //pink

            case (13882323) -> 10329495; //light_gray
            case (8421504) -> 4673362; //gray
            default -> argb;
        };
    }
}
