package net.greenjab.fixedminecraft.mixin.client.particle;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.guardian.GuardianParticleModel;
import net.minecraft.client.particle.ElderGuardianParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElderGuardianParticle.class)
public abstract class ElderGuardianParticleMixin {

    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/client/model/geom/ModelPart;)Lnet/minecraft/client/model/monster/guardian/GuardianParticleModel;"))
    private GuardianParticleModel phantom(GuardianParticleModel original,
                                          @Local(argsOnly = true, ordinal = 1) double y) {
        if (y<-500) {
            return new GuardianParticleModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PHANTOM));
        }
        return original;
    }
}
