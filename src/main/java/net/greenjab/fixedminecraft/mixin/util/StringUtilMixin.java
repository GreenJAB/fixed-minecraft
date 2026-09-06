package net.greenjab.fixedminecraft.mixin.util;

import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(StringUtil.class)
public abstract class StringUtilMixin {
    @ModifyConstant(method = "isAllowedChatCharacter", constant = @Constant(intValue = 167))
    private static int allowFormatChar(int constant) {
        return 0;
    }
}
