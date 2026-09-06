package net.greenjab.fixedminecraft.mixin.client.font;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BookViewScreen.class)
public abstract class BookViewScreenMixin {

    @WrapOperation(method = "visitText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;split(Lnet/minecraft/network/chat/FormattedText;I)Ljava/util/List;"))
    private List<FormattedCharSequence> makeSplitLinesIdentifiable(Font instance, FormattedText input, int maxWidth,
                                                                   Operation<List<FormattedCharSequence>> original) {
        return Language.getInstance().getVisualOrder(splitLines(instance.getSplitter(), input, maxWidth, Style.EMPTY));
    }

    @Unique public List<FormattedText> splitLines(StringSplitter splitter, final FormattedText input, final int maxWidth, final Style initialStyle) {
        List<FormattedText> result = Lists.newArrayList();
        splitter.splitLines(input, maxWidth, initialStyle,  (text, _) -> result.add(text));
        return result;
    }
}
