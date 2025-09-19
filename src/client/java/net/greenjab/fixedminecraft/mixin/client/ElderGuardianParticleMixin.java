package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ElderGuardianParticleModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.ElderGuardianParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElderGuardianParticle.class)
public class ElderGuardianParticleMixin {

    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/client/model/ModelPart;)Lnet/minecraft/client/model/ElderGuardianParticleModel;"))
    private ElderGuardianParticleModel phantom(ElderGuardianParticleModel original, @Local(argsOnly = true, ordinal = 1) double y) {
        if (y<-500) {
            return new ElderGuardianParticleModel(MinecraftClient.getInstance().getLoadedEntityModels().getModelPart(EntityModelLayers.PHANTOM));
        }
        return original;
    }

    /*@Redirect(method = "renderCustom", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
    private void phantom(Model instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        ElderGuardianParticle particle = (ElderGuardianParticle)(Object)this;
        if (particle.y<-500) {
            RenderLayer layer = RenderLayer.getEntityTranslucent(Identifier.of("textures/entity/phantom.png"));
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer vertexConsumer2 = immediate.getBuffer(layer);
            Model model = new PhantomEntityModel(MinecraftClient.getInstance()
                    .getLoadedEntityModels()
                    .getModelPart(EntityModelLayers.PHANTOM));
            matrices.scale(2F, 2F, 2F);
            model.render(matrices, vertexConsumer2, light, overlay, color);
        } else {
            instance.render(matrices, vertices, light, overlay, color);
        }
    }

    @ModifyArg(method = "renderCustom", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"), index = 0)
    private float v(float deg, @Local(ordinal = 1)float f){
        ElderGuardianParticle particle = (ElderGuardianParticle)(Object)this;
        if (particle.y<-500) {
            return 150.0F * (1-(0.5f*f+0.2f)) - 60.0F;
        } else {
            return 150.0F * f - 60.0F;
        }
    }*/
}
