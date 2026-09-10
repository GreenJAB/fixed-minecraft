package net.greenjab.fixedminecraft.mixin.client.font;

import com.mojang.blaze3d.platform.InputConstants;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.hud.HotbarCycler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "handleGlobalKeyPress(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z", ordinal = 0), cancellable = true)
    private void toggleFontHelper(InputConstants.Key key, boolean controlDown, CallbackInfoReturnable<Boolean> cir) {
        if (HotbarCycler.getFontLegendKeyBinding().matches(key)) {
            FixedMinecraftClient.fontLegend =!FixedMinecraftClient.fontLegend;
            cir.setReturnValue(true);
        }
    }
}
