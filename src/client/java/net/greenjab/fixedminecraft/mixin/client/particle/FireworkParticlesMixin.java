package net.greenjab.fixedminecraft.mixin.client.particle;


import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkParticles.Starter.class)
public abstract class FireworkParticlesMixin extends NoRenderParticle {

    @Shadow
    protected abstract void createParticle(double x, double y, double z, double xa, double ya, double za,
                                           IntList rgbColors, IntList fadeColors, boolean trail, boolean twinkle);

    protected FireworkParticlesMixin(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    @Inject(method = "createParticleBurst", at=@At("HEAD"), cancellable = true)
    private void cube(IntList rgbColors, IntList fadeColors, boolean trail, boolean twinkle, CallbackInfo ci) {
        double d = this.x;
        double e = this.y;
        double f = this.z;

        int amount = 4;
        double size = 0.5;
        double ang = this.random.nextDouble()*Math.PI*2.0;
        double ax = Math.cos(ang);
        double az = Math.sin(ang);

        for (int i = -amount; i <= amount; i++) {
            for (int j = -amount; j <= amount; j++) {
                for (int k = -amount; k <= amount; k++) {
                    double g = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                    double h = (double)i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                    double l = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                    double m = Math.sqrt(g * g + h * h + l * l) / size + this.random.nextGaussian() * 0.05;
                    double s = Math.cos(Math.PI/4.0)*size;
                    g = Mth.clamp((g / m) * 1.5, -s, s);
                    h = Mth.clamp((h/m)*1.5, -s, s);
                    l = Mth.clamp((l/m)*1.5, -s, s);
                    double gg = g;
                    g = g*ax+l*az;
                    l = l*ax-gg*az;
                    this.createParticle(d, e, f, g,h,l, rgbColors, fadeColors, trail, twinkle);
                    if (i != -amount && i != amount && j != -amount && j != amount) {
                        k += amount * 2 - 1;
                    }
                }
            }
        }
        ci.cancel();
    }
}
