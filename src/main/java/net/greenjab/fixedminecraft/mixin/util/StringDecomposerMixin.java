package net.greenjab.fixedminecraft.mixin.util;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StringDecomposer.class)
public abstract class StringDecomposerMixin {
    @ModifyConstant(method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z", constant = @Constant(intValue = 167))
    private static int atSymbolFormatChar(int constant, @Local char ch, @Local(argsOnly = true)String string, @Local(ordinal = 1) int size, @Local(ordinal = 2) int i) {
        if (ch == 37) {
            while (i + 2 < size) {
                if (ChatFormatting.getByCode(string.charAt(i + 1)) == null) return constant;
                if (string.charAt(i + 2)==37) i+=2;
                else return 37;
            }
            return constant;
        }
        return constant;
    }
}
