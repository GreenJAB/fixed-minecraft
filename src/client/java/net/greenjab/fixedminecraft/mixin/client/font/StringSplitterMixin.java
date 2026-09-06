package net.greenjab.fixedminecraft.mixin.client.font;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Mixin(StringSplitter.class)
public abstract class StringSplitterMixin {

    @WrapOperation(method = "splitLines(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/network/chat/Style;Ljava/util/function/BiConsumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/StringSplitter$LineBreakFinder;getSplitStyle()Lnet/minecraft/network/chat/Style;"))
    private Style resetNewLineStyle(StringSplitter.LineBreakFinder instance, Operation<Style> original, @Local(argsOnly = true) BiConsumer<FormattedText, Boolean> output) {
        if (output.toString().contains("BookViewScreen")) return Style.EMPTY.withoutShadow().withColor(-16777216);
        return original.call(instance);
    }
}
