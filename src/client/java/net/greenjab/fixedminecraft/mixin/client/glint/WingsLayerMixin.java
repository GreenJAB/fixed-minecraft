package net.greenjab.fixedminecraft.mixin.client.glint;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(WingsLayer.class)
@Environment(EnvType.CLIENT)
public abstract class WingsLayerMixin<S extends HumanoidRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {

    public WingsLayerMixin(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state,
                                               float yRot, float xRot, CallbackInfo ci) {
        EnchantGlint.setTargetStack(state.chestEquipment);
    }
}
