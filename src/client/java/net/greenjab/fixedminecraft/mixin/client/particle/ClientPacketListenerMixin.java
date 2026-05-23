package net.greenjab.fixedminecraft.mixin.client.particle;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Redirect(method = "handleGameEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void phantomParticle(ClientLevel instance, ParticleOptions particle, double x, double y, double z, double xd, double yd,
                                 double zd,
                                 @Local float paramFloat) {
        if (paramFloat == 2) {
            instance.addParticle(particle, x, y-1000, z, 0,0,0);
        } else {
            instance.addParticle(particle, x, y, z, 0,0,0);
        }
    }
}
