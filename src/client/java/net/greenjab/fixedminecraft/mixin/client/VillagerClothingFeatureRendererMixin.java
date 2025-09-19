package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VillagerClothingFeatureRenderer.class)
public class VillagerClothingFeatureRendererMixin <S extends LivingEntityRenderState>{

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/LivingEntityRenderState;FF)V",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/VillagerClothingFeatureRenderer;getTexture(Ljava/lang/String;Lnet/minecraft/registry/entry/RegistryEntry;)Lnet/minecraft/util/Identifier;"))
    private String leggingsRemoveHalfOfCloak (String keyType, @Local(argsOnly = true) S livingEntityRenderState){
        if (livingEntityRenderState.headItemAnimationProgress == 1) return keyType + "_half";
        return keyType;
    }
}
