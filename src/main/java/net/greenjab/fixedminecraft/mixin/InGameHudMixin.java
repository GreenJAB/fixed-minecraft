package net.greenjab.fixedminecraft.mixin;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.greenjab.fixedminecraft.client.HUDOverlayHandler;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
    @Inject(at = @At(value = "CONSTANT", args = "stringValue=food", shift = At.Shift.BY, by = 2), method = "renderStatusBars")
    private void renderFoodPre(DrawContext context, CallbackInfo info)
    {
        if (HUDOverlayHandler.INSTANCE != null)
            HUDOverlayHandler.INSTANCE.onPreRender(context);
    }

    @Inject(slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=food")), at = @At(value = "net.green_jab.fixed_minecraft.mixin.util.BeforeInc", args = "intValue=-10", ordinal = 0), method = "renderStatusBars")
    private void renderFoodPost(DrawContext context, CallbackInfo info)
    {
        if (HUDOverlayHandler.INSTANCE != null)
            HUDOverlayHandler.INSTANCE.onRender(context);
    }
}
