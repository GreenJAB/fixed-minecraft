package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.MapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public abstract class MapRendererMixin {

    @Inject(method = "render",
            at = @At(value = "INVOKE",
              target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
    ))
    private void scalePlayerMarkerWithDistance(MapRenderState mapRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                               boolean showOnlyFrame, int lightCoords, CallbackInfo ci,
                                               @Local MapRenderState.MapDecorationRenderState decoration){
        int rot = decoration.rot;
        if (rot < 0) rot+=256;
        rot/=16;
        float scale = rot/15.0f;
        if (scale<0.001) return;
        if (scale >0.999) poseStack.scale(0, 0, 0);
        scale = 0.4f*scale+0.45f;
        poseStack.scale(scale, scale, scale);
    }
}
