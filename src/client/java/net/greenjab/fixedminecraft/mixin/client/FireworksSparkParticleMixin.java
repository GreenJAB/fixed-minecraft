package net.greenjab.fixedminecraft.mixin.client;


import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworksSparkParticle.FireworkParticle.class)
public abstract class FireworksSparkParticleMixin extends NoRenderParticle {
    @Shadow
    protected abstract void addExplosionParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ,
                                                 int[] colors, int[] fadeColors, boolean trail, boolean flicker);

    protected FireworksSparkParticleMixin(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Inject(method = "explodeBurst", at=@At("HEAD"), cancellable = true)
    private void cube(int[] colors, int[] fadeColors, boolean trail, boolean flicker, CallbackInfo ci) {
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
                    g=MathHelper.clamp((g/m)*1.5, -s, s);
                    h=MathHelper.clamp((h/m)*1.5, -s, s);
                    l=MathHelper.clamp((l/m)*1.5, -s, s);
                    double gg = g;
                    g = g*ax+l*az;
                    l = l*ax-gg*az;
                    this.addExplosionParticle(d, e, f, g,h,l, colors, fadeColors, trail, flicker);
                    if (i != -amount && i != amount && j != -amount && j != amount) {
                        k += amount * 2 - 1;
                    }
                }
            }
        }
        ci.cancel();
    }
}
