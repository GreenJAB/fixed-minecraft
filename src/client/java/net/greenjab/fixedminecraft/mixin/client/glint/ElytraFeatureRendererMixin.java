package net.greenjab.fixedminecraft.mixin.client.glint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ElytraFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    public ElytraFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render*", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int i,
            T livingEntity,
            float f,
            float g,
            float h,
            float j,
            float k,
            float l,
            CallbackInfo ci) {
        ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        EnchantGlint.setTargetStack(itemStack);
    }
}
