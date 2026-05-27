package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {

    @Inject(method = "setFocused", at = @At("HEAD"), cancellable = true)
    private void dontFocusRecipeButton(boolean focused, CallbackInfo ci) {
        if (((AbstractWidget)(Object)this) instanceof ImageButton imageButton && imageButton.getMessage().getString().toLowerCase().contains("recipe_book")) {
            ci.cancel();
        }
    }
}
