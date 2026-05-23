package net.greenjab.fixedminecraft.mixin.client.glint;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(SimpleEquipmentLayer.class)
public abstract class SimpleEquipmentLayerMixin<S extends LivingEntityRenderState> {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
    ))
    private void setEnchantTheRainbowItemStack(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
                                               S state, float yRot, float xRot, CallbackInfo ci, @Local ItemStack equipment) {
        EnchantGlint.setTargetStack(equipment);
    }
}
