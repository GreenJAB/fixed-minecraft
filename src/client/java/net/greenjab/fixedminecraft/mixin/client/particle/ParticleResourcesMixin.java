package net.greenjab.fixedminecraft.mixin.client.particle;

import net.greenjab.fixedminecraft.registry.registries.ParticleRegistry;
import net.greenjab.fixedminecraft.render.VillagerParticle;
import net.minecraft.client.particle.FlyTowardsPositionParticle;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleResources.class)
public abstract class ParticleResourcesMixin {

    @Shadow
    protected abstract <T extends ParticleOptions> void register(ParticleType<T> type,
                                                                 ParticleResources.SpriteParticleRegistration<T> provider);

    @Inject(method = "registerProviders", at = @At(value = "HEAD"))
    private void addNumberParticle(CallbackInfo ci) {
        register(ParticleRegistry.VILLAGER_HUNGRY, VillagerParticle.Hungry::new);
        register(ParticleRegistry.VILLAGER_TIRED, VillagerParticle.Tired::new);
        register(ParticleRegistry.VILLAGER_LONELY, VillagerParticle.Lonely::new);
        register(ParticleRegistry.VILLAGER_NIGHT, VillagerParticle.Night::new);
        register(ParticleRegistry.VILLAGER_DARK, VillagerParticle.Dark::new);
        register(ParticleRegistry.VILLAGER_LAZY, VillagerParticle.Lazy::new);
        register(ParticleRegistry.CHISELED_ENCHANT, FlyTowardsPositionParticle.EnchantProvider::new);
    }
}
