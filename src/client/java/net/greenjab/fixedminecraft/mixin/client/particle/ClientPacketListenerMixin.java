package net.greenjab.fixedminecraft.mixin.client.particle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @WrapOperation(method = "handleGameEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void phantomParticle(ClientLevel instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, Operation<Void> original, @Local float paramFloat) {
        if (paramFloat == 2) original.call(instance, particle, x, y-1000, z, xd, yd, zd);
        else original.call(instance, particle, x, y, z, xd, yd, zd);
    }
}
