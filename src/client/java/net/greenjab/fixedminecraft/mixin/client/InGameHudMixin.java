package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.hud.HUDOverlayHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
     @Inject(
             slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=food")),
             at = @At(value = "net.greenjab.fixedminecraft.mixin.util.BeforeInc", args = "intValue=-10", ordinal = 0),
             method = "renderStatusBars"
     )
     private void renderFoodPost(DrawContext context, CallbackInfo info) {
         HUDOverlayHandler.onRender(context);
     }
}
