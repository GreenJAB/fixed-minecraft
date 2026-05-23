package net.greenjab.fixedminecraft.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

public class VillagerParticle extends SingleQuadParticle {
    private VillagerParticle(
            final ClientLevel level,
            final double x,
            final double y,
            final double z,
            final double xa,
            final double ya,
            final double za,
            final TextureAtlasSprite sprite
    ) {
        super(level, x, y, z, sprite);
        this.scale(1.5F);
        this.setSize(0.25F, 0.25F);
        this.lifetime = this.random.nextInt(20) + 50;

        this.gravity = 3.0E-6F;
        this.xd = xa;
        this.yd = ya + this.random.nextFloat() / 500.0F;
        this.zd = za;
        this.alpha = 0.05F;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            this.xd = this.xd + this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
            this.zd = this.zd + this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
            this.yd = this.yd - this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.age <= 5) {
                this.alpha += 0.2F;
                if (this.alpha > 1) this.alpha = 1;
            } else if (this.age >= this.lifetime - 20 && this.alpha > 0.01F) {
                this.alpha -= 0.04F;
            }
        } else {
            this.remove();
        }
    }

    @Override
    public SingleQuadParticle.@NonNull Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    public static class Hungry implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Hungry(final SpriteSet sprites) { this.sprites = sprites; }

        public Particle createParticle(
                final @NonNull SimpleParticleType options, final @NonNull ClientLevel level,
                final double x, final double y, final double z,
                final double xAux, final double yAux, final double zAux,
                final @NonNull RandomSource random
        ) {
            return new VillagerParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(random));
        }
    }

    public static class Tired implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Tired(final SpriteSet sprites) { this.sprites = sprites; }

        public Particle createParticle(
                final @NonNull SimpleParticleType options, final @NonNull ClientLevel level,
                final double x, final double y, final double z,
                final double xAux, final double yAux, final double zAux,
                final @NonNull RandomSource random
        ) {
            return new VillagerParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(random));
        }
    }

    public static class Lonely implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Lonely(final SpriteSet sprites) { this.sprites = sprites; }

        public Particle createParticle(
                final @NonNull SimpleParticleType options, final @NonNull ClientLevel level,
                final double x, final double y, final double z,
                final double xAux, final double yAux, final double zAux,
                final @NonNull RandomSource random
        ) {
            return new VillagerParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(random));
        }
    }

    public static class Dark implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Dark(final SpriteSet sprites) { this.sprites = sprites; }

        public Particle createParticle(
                final @NonNull SimpleParticleType options, final @NonNull ClientLevel level,
                final double x, final double y, final double z,
                final double xAux, final double yAux, final double zAux,
                final @NonNull RandomSource random
        ) {
            return new VillagerParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(random));
        }
    }

    public static class Lazy implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Lazy(final SpriteSet sprites) { this.sprites = sprites; }

        public Particle createParticle(
                final @NonNull SimpleParticleType options, final @NonNull ClientLevel level,
                final double x, final double y, final double z,
                final double xAux, final double yAux, final double zAux,
                final @NonNull RandomSource random
        ) {
            return new VillagerParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(random));
        }
    }

    public static class Night implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Night(final SpriteSet sprites) { this.sprites = sprites; }

        public Particle createParticle(
                final @NonNull SimpleParticleType options, final @NonNull ClientLevel level,
                final double x, final double y, final double z,
                final double xAux, final double yAux, final double zAux,
                final @NonNull RandomSource random
        ) {
            return new VillagerParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(random));
        }
    }
}
