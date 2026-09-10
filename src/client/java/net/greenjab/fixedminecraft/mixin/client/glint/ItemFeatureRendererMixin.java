package net.greenjab.fixedminecraft.mixin.client.glint;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Credit: Pepperoni-Jabroni */
@Mixin(ItemFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemFeatureRendererMixin {

    @Shadow
    private static boolean useTransparentGlint(RenderType renderType) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @ModifyArg(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer;getVertexBuilder(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static RenderType getGlintTranslucent(RenderType par1, @Local(argsOnly = true) RenderType renderType, @Local PoseStack.@Nullable Pose foilDecalPose) {
        boolean green = foilDecalPose != null;
        if (useTransparentGlint(renderType)) return EnchantGlint.getGlintTranslucent(green);
        else return EnchantGlint.getGlint(green);
    }
}
