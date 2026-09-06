package net.greenjab.fixedminecraft.mixin.client.font;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.hud.HotbarCycler;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

    @Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(Lnet/minecraft/client/input/KeyEvent;)Z", ordinal = 0),
            cancellable = true
    )
    private void toggleFontHelper(long handle, int action, KeyEvent event, CallbackInfo ci) {
        if (HotbarCycler.getFontLegendKeyBinding().matches(event)) {
            FixedMinecraftClient.fontLegend =!FixedMinecraftClient.fontLegend;
            ci.cancel();
        }
    }

}
