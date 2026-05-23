package net.greenjab.fixedminecraft.mixin.client.particle;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ElderGuardianParticle;
import net.minecraft.client.particle.ElderGuardianParticleGroup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElderGuardianParticleGroup.ElderGuardianParticleRenderState.class)
public abstract class ElderGuardianParticleRenderStateMixin {
    @Unique
    private static final RenderType renderLayer2 = RenderTypes.entityTranslucent(Identifier.withDefaultNamespace("textures/entity/phantom/phantom.png"));

    @ModifyExpressionValue(method = "fromParticle", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/particle/ElderGuardianParticle;renderType:Lnet/minecraft/client/renderer/rendertype/RenderType;",
            opcode = Opcodes.GETFIELD
    ))
    private static RenderType phantomTexture(RenderType original,
                                             @Local(argsOnly = true) ElderGuardianParticle particle) {
        if (particle.y<-500) {
            return renderLayer2;
        }
        return original;
    }

    @Inject(method = "fromParticle", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private static void phantomSize(ElderGuardianParticle particle, Camera camera, float partialTickTime,
                                    CallbackInfoReturnable<ElderGuardianParticleGroup.ElderGuardianParticleRenderState> cir,
                                    @Local PoseStack poseStack) {
        if (particle.y<-500) {
            poseStack.translate(0.0F, 0F, -2F);
        }
    }

    @ModifyArg(method = "fromParticle", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;"))
    private static float phantomSize(float deg, @Local(argsOnly = true) ElderGuardianParticle particle) {
        if (particle.y<-500) {
            deg = -60-deg;
        }
        return deg;
    }

}
