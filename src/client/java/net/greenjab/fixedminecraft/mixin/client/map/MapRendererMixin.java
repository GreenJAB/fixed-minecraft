package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.command.EntityRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public class MapRendererMixin {

    @Inject(method = "draw",
            at = @At(value = "INVOKE",
              target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionfc;)V"
    ))
    private void scalePlayerMarkerWithDistance(MapRenderState state, MatrixStack matrices,
                                               EntityRenderCommandQueue entityRenderCommandQueue, boolean bl, int light, CallbackInfo ci,
                                               @Local MapRenderState.Decoration decoration){
        int rot = decoration.rotation;
        if (rot < 0) rot+=256;
        rot/=16;
        float scale = rot/15.0f;
        if (scale<0.001) return;
        if (scale >0.999) matrices.scale(0, 0, 0);
        scale = 0.4f*scale+0.45f;
        matrices.scale(scale, scale, scale);
    }
}
