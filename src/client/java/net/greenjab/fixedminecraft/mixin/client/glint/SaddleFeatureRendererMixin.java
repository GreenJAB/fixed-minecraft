package net.greenjab.fixedminecraft.mixin.client.glint;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(SaddleFeatureRenderer.class)
public abstract class SaddleFeatureRendererMixin<S extends LivingEntityRenderState, RM extends EntityModel<? super S>, EM extends EntityModel<? super S>> {

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/LivingEntityRenderState;FF)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;"
    ))
    private void setEnchantTheRainbowItemStack(MatrixStack matrixStack, OrderedRenderCommandQueue entityRenderCommandQueue, int i,
                                               S livingEntityRenderState, float f, float g, CallbackInfo ci, @Local ItemStack itemStack) {
        EnchantGlint.setTargetStack(itemStack);
    }
}
