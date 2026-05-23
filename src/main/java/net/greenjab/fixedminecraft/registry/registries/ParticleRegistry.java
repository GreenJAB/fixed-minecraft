package net.greenjab.fixedminecraft.registry.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ParticleRegistry {

    public static final SimpleParticleType VILLAGER_HUNGRY = register("villager_hungry", true);
    public static final SimpleParticleType VILLAGER_TIRED = register("villager_tired", true);
    public static final SimpleParticleType VILLAGER_LONELY = register("villager_lonely", true);
    public static final SimpleParticleType VILLAGER_DARK = register("villager_dark", true);
    public static final SimpleParticleType VILLAGER_LAZY = register("villager_lazy", true);
    public static final SimpleParticleType VILLAGER_NIGHT = register("villager_night", true);
    public static final SimpleParticleType CHISELED_ENCHANT = register("chiseled_enchant", false);

    private static SimpleParticleType register(final String name, final boolean overrideLimiter) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, name, new SimpleParticleType(overrideLimiter));
    }

    public static void registerParticles() {
        System.out.println("register Particles");
    }
}
