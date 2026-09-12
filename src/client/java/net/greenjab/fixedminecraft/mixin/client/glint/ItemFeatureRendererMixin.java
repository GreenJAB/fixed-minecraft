package net.greenjab.fixedminecraft.mixin.client.glint;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemFeatureRendererMixin {

    /*@Shadow
    private static boolean useTransparentGlint(RenderType renderType) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @ModifyArg(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer;getVertexBuilder(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static RenderType getGlintTranslucent(RenderType par1, @Local(argsOnly = true) RenderType renderType, @Local(argsOnly = true) PoseStack.@Nullable Pose foilDecalPose) {
        boolean green = foilDecalPose != null;
        if (useTransparentGlint(renderType)) return EnchantGlint.getGlintTranslucent(green);
        else return EnchantGlint.getGlint(green);
    }*/
    //TODO why must mojang do this to me

    @WrapOperation(method = "prepareMainSubmit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;itemGlintSpecialRenderType()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType getGlintTranslucent(BakedQuad.MaterialInfo instance, Operation<RenderType> original) {
        return EnchantGlint.getGlint();
    }
}
