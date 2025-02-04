package net.greenjab.fixedminecraft.mixin.client.glint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
@Environment(EnvType.CLIENT)
public class ItemRendererMixin {
    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode,
                                               boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                               World world, int light, int overlay, int seed, CallbackInfo ci) {
       // EnchantGlint.setTargetStack(stack);
    }


    @Redirect(method = "getArmorGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getArmorEntityGlint() {
        return EnchantGlint.getArmorEntityGlint();
    }

    @Redirect(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getGlint() {
        return EnchantGlint.getGlint();
    }

    @Redirect(method = "getItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getEntityGlint() {
        return EnchantGlint.getEntityGlint();
    }

    /*@Redirect(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getDirectGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getGlintDirect() {
        return EnchantGlint.getGlintDirect();
    }

    @Redirect(method = "getDirectItemGlintConsumer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getDirectEntityGlint()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer getEntityGlintDirect() {
        return EnchantGlint.getEntityGlintDirect();
    }*/

}
