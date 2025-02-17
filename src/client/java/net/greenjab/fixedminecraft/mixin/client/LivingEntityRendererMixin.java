package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.models.HumanArmorFeatureRenderer;
import net.greenjab.fixedminecraft.models.ModelLayers;
import net.greenjab.fixedminecraft.models.VillagerArmorModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>{

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/EntityRenderState;FF)V"
    ))
    public void addVillagerArmorLayer(S livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                      int i, CallbackInfo ci) {
        //System.out.println("AAAAAAA "+livingEntityRenderState.toString() + ", " + (livingEntityRenderState instanceof VillagerEntityRenderState) + (livingEntityRenderState instanceof BipedEntityRenderState));
    }
}
