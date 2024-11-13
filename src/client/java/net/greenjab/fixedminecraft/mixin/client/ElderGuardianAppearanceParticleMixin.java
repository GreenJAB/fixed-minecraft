package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.ElderGuardianAppearanceParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ElderGuardianAppearanceParticle.class)
public class ElderGuardianAppearanceParticleMixin {
    @Redirect(method = "buildGeometry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void phantom(Model instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, float r, float g, float b,
                         float g2) {
        ElderGuardianAppearanceParticle particle = (ElderGuardianAppearanceParticle)(Object)this;
        if (particle.y<-500) {
            RenderLayer layer = RenderLayer.getEntityTranslucent(new Identifier("textures/entity/phantom.png"));
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer vertexConsumer2 = immediate.getBuffer(layer);
            Model model = new PhantomEntityModel<>(MinecraftClient.getInstance()
                    .getEntityModelLoader()
                    .getModelPart(EntityModelLayers.PHANTOM));
            model.render(matrixStack, vertexConsumer2, light, uv, r, g, b, g2);
        } else {
            instance.render(matrixStack, vertexConsumer, light, uv, r, g, b, g2);
        }
    }

    @ModifyArg(method = "buildGeometry", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"), index = 0)
    private float v(float deg, @Local(ordinal = 1)float f){
        ElderGuardianAppearanceParticle particle = (ElderGuardianAppearanceParticle)(Object)this;
        if (particle.y<-500) {
            return 150.0F * (1-f) - 60.0F;
        } else {
            return 150.0F * f - 60.0F;
        }
    }
}
