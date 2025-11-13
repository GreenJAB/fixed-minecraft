package net.greenjab.fixedminecraft.mixin.client.glint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(ElytraFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ElytraFeatureRendererMixin<S extends BipedEntityRenderState, M extends EntityModel<S>> extends FeatureRenderer<S, M> {

    public ElytraFeatureRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(MatrixStack matrixStack, OrderedRenderCommandQueue entityRenderCommandQueue, int i,
                                               S bipedEntityRenderState, float f, float g, CallbackInfo ci) {
        EnchantGlint.setTargetStack(bipedEntityRenderState.equippedChestStack);
    }
}
