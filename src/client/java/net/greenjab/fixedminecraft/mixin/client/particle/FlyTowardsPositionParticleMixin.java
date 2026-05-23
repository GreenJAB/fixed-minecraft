package net.greenjab.fixedminecraft.mixin.client.particle;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.particle.FlyTowardsPositionParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FlyTowardsPositionParticle.class)
public abstract class FlyTowardsPositionParticleMixin {

    @ModifyVariable(
            method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDZLnet/minecraft/client/particle/Particle$LifetimeAlpha;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static boolean glow(boolean isGlowing, @Local(argsOnly = true) TextureAtlasSprite sprite) {
        if (sprite.toString().toLowerCase().contains("chiseled")) return true;
        return isGlowing;
    }
}
