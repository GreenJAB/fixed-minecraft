package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Redirect(method = "onGameStateChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void phantomParticle(ClientWorld instance, ParticleEffect particle, double x, double y, double z, double velocityX,
                                 double velocityY, double velocityZ,
                                 @Local float f) {
        if (f == 2) {
            instance.addParticleClient(particle, x, y-1000, z, 0,0,0);
        } else {
            instance.addParticleClient(particle, x, y, z, 0,0,0);
        }
    }
}
