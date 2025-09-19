package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.particle.ElderGuardianParticle;
import net.minecraft.client.particle.ElderGuardianParticleRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElderGuardianParticleRenderer.State.class)
public class ElderGuardianParticleRendererMixin {
    private static final RenderLayer renderLayer2 = RenderLayer.getEntityTranslucent(Identifier.ofVanilla("textures/entity/phantom.png"));

    @ModifyExpressionValue(method = "create", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/ElderGuardianParticle;renderLayer:Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer phantomTexture(RenderLayer original,
                                       @Local(argsOnly = true) ElderGuardianParticle particle) {
        if (particle.y<-500) {
            return renderLayer2;
        }
        return original;
    }

    @Inject(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private static void phantomSize(ElderGuardianParticle particle, Camera camera, float tickProgress,
                                    CallbackInfoReturnable<ElderGuardianParticleRenderer.State> cir, @Local MatrixStack matrixStack) {
        if (particle.y<-500) {
            matrixStack.translate(0.0F, 0F, -2F);
        }
    }

    @ModifyArg(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"))
    private static float phantomSize(float deg, @Local(argsOnly = true) ElderGuardianParticle particle) {
        if (particle.y<-500) {
            deg = -60-deg;
        }
        return deg;
    }

}
