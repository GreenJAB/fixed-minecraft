package net.greenjab.fixedminecraft.mixin.client.glint;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Credit: Pepperoni-Jabroni */
@Mixin(ItemFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemFeatureRendererMixin {

    @Redirect(method = "getFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;)Lcom/mojang/blaze3d/vertex/VertexConsumer;", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer;getFoilRenderType(Lnet/minecraft/client/renderer/rendertype/RenderType;Z)Lnet/minecraft/client/renderer/rendertype/RenderType;"
    ))
    private static RenderType new_getItemGlintConsumer(RenderType baseRenderType, boolean sheeted,
                                                       @Local(argsOnly = true) PoseStack.Pose foilDecalPose){
         return getFoilRenderType(baseRenderType, true, foilDecalPose);
    }

    @Redirect(method = "getFoilRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType getGlint() {
        return EnchantGlint.getGlint();
    }

    @Redirect(method = "getFoilRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glintTranslucent()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType getGlintTranslucent() {
        return EnchantGlint.getGlintTranslucent();
    }

    @Redirect(method = "getFoilRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entityGlint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private static RenderType getEntityGlint() {
        return EnchantGlint.getEntityGlint();
    }

    @Unique
    private static RenderType getFoilRenderType(RenderType baseRenderType, boolean sheeted,
                                                   PoseStack.Pose foilDecalPose) {
        boolean special = foilDecalPose!=null;

        if (useTransparentGlint(baseRenderType)) {
            return EnchantGlint.getGlintTranslucent(special);
        } else {
            return sheeted ? EnchantGlint.getGlint(special) : EnchantGlint.getEntityGlint(special);
        }
    }
    @Unique
    private static boolean useTransparentGlint(RenderType renderType) {
        return Minecraft.useShaderTransparency() && renderType.outputTarget() == OutputTarget.ITEM_ENTITY_TARGET;
    }//*/
}
