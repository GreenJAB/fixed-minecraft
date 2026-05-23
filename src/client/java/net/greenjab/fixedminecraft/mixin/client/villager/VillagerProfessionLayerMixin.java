package net.greenjab.fixedminecraft.mixin.client.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VillagerProfessionLayer.class)
public abstract class VillagerProfessionLayerMixin<S extends LivingEntityRenderState>{

    @ModifyArg(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/VillagerProfessionLayer;getIdentifier(Ljava/lang/String;Lnet/minecraft/core/Holder;)Lnet/minecraft/resources/Identifier;"), index = 0)
    private String leggingsRemoveHalfOfCloak (String keyType, @Local(argsOnly = true) S state){
        if (state.wornHeadAnimationPos == 1) return keyType + "_half";
        return keyType;
    }
}
