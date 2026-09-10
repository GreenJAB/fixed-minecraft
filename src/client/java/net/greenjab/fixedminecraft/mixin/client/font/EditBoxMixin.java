package net.greenjab.fixedminecraft.mixin.client.font;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
    @WrapOperation(method = "applyFormat", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/util/FormattedCharSequence;forward(Ljava/lang/String;Lnet/minecraft/network/chat/Style;)Lnet/minecraft/util/FormattedCharSequence;"))
    private FormattedCharSequence useFormatedText(String plainText, Style style, Operation<FormattedCharSequence> original) {
        return Component.literal(plainText).getVisualOrderText();
    }
}
