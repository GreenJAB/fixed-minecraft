package net.greenjab.fixedminecraft.mixin.client.glint;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Credit: Pepperoni-Jabroni */
@Mixin(ItemRenderer.class)
@Environment(EnvType.CLIENT)
public class ItemRendererMixin {

    @ModifyExpressionValue(method = "renderItem(Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILjava/util/List;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/item/ItemRenderState$Glint;SPECIAL:Lnet/minecraft/client/render/item/ItemRenderState$Glint;"
    ))
    private static ItemRenderState.Glint removeNormalSpecialGlint(ItemRenderState.Glint original, @Local(argsOnly = true) ItemRenderState.Glint glint){
        if (glint  == ItemRenderState.Glint.SPECIAL) {
            return ItemRenderState.Glint.STANDARD;
        } else {
            return ItemRenderState.Glint.SPECIAL;
        }
    }

    @Redirect(method = "renderItem(Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILjava/util/List;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/item/ItemRenderer;getItemGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"
    ))
    private static VertexConsumer new_getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint, @Local(argsOnly = true) ItemRenderState.Glint specialglint){
         return getItemGlintConsumer(vertexConsumers, layer, solid, glint, specialglint);
    }


    @Redirect(method = "getArmorGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getArmorEntityGlint() {
        return EnchantGlint.getArmorEntityGlint();
    }

    @Redirect(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getGlint() {
        return EnchantGlint.getGlint();
    }

    @Redirect(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlintTranslucent()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getGlintTranslucent() {
        return EnchantGlint.getGlintTranslucent();
    }

    @Redirect(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getEntityGlint() {
        return EnchantGlint.getEntityGlint();
    }


    @Unique
    private static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid,
                                                       boolean glint, ItemRenderState.Glint specialglint) {
        boolean special = specialglint == ItemRenderState.Glint.SPECIAL;
        if (glint) {
            return method_71139(layer)
                    ? VertexConsumers.union(vertexConsumers.getBuffer(EnchantGlint.getGlintTranslucent(special)), vertexConsumers.getBuffer(layer))
                    : VertexConsumers.union(vertexConsumers.getBuffer(solid ? EnchantGlint.getGlint(special) : EnchantGlint.getEntityGlint(special)), vertexConsumers.getBuffer(layer));
        } else {
            return vertexConsumers.getBuffer(layer);
        }
    }
    @Unique
    private static boolean method_71139(RenderLayer renderLayer) {
        return MinecraftClient.isFabulousGraphicsOrBetter() && renderLayer == TexturedRenderLayers.getItemEntityTranslucentCull();
    }
}
