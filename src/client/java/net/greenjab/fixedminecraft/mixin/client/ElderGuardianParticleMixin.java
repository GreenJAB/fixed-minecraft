package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ElderGuardianParticleModel;
import net.minecraft.client.particle.ElderGuardianParticle;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElderGuardianParticle.class)
public class ElderGuardianParticleMixin {

    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/client/model/ModelPart;)Lnet/minecraft/client/model/ElderGuardianParticleModel;"))
    private ElderGuardianParticleModel phantom(ElderGuardianParticleModel original, @Local(argsOnly = true, ordinal = 1) double y) {
        if (y<-500) {
            return new ElderGuardianParticleModel(MinecraftClient.getInstance().getLoadedEntityModels().getModelPart(EntityModelLayers.PHANTOM));
        }
        return original;
    }
}
