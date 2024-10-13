package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.hud.HUDOverlayHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonEntityRenderer.DragonEntityModel.class)
public class EnderDragonEntityRendererMixin {

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 5.0f, ordinal = 2))
    private float properTailDirection(float value) {
        return -value/2.0f;
    }
}
