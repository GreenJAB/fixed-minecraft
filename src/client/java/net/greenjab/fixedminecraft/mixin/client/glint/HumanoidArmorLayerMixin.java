package net.greenjab.fixedminecraft.mixin.client.glint;

import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {

    public HumanoidArmorLayerMixin(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                               net.minecraft.world.item.ItemStack itemStack, net.minecraft.world.entity.EquipmentSlot slot,
                                               int lightCoords, S state, CallbackInfo ci) {
        EnchantGlint.setTargetStack(itemStack);
    }
}
