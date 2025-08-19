package net.greenjab.fixedminecraft.mixin.client.glint;

import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.render.entity.command.EntityRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin <S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {

    public ArmorFeatureRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Inject(method = "renderArmor", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(MatrixStack matrices, EntityRenderCommandQueue entityRenderCommandQueue, ItemStack stack,
                                               EquipmentSlot slot, int light, S bipedEntityRenderState, CallbackInfo ci) {
        EnchantGlint.setTargetStack(stack);
    }
}
