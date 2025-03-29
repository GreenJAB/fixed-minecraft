package net.greenjab.fixedminecraft.mixin.client.glint;

import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
//import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.equipment.ArmorMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//TODO test and remove
/** Credit: Pepperoni-Jabroni */
//@Mixin(HorseArmorFeatureRenderer.class)
@Mixin(ArmorMaterial.class)
public abstract class HorseArmorFeatureRendererMixin/*<S extends BipedEntityRenderState, M extends BipedEntityModel<S>> extends FeatureRenderer<S, M> */{
   /* public HorseArmorFeatureRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/HorseEntityRenderState;FF)V", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
                                               HorseEntityRenderState horseEntityRenderState, float f, float g, CallbackInfo ci) {
        EnchantGlint.setTargetStack(horseEntityRenderState.armor);
    }*/
}
