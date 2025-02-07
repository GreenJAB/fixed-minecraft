package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "attemptTickInVoid", at = @At(
            value = "HEAD"
    ), cancellable = true)
    private void tridentReturnsFromVoid(CallbackInfo ci) {
        Entity E = (Entity) (Object)this;
        if (E instanceof TridentEntity TE) {
            if (TE.getY() < (double)(TE.getWorld().getBottomY() - 48)) {
                //int i = EnchantmentHelper.getLevel(Enchantments.LOYALTY,TE.getItemStack());
                int i = TE.getWorld() instanceof ServerWorld serverWorld
                        ? (byte) MathHelper.clamp(EnchantmentHelper.getTridentReturnAcceleration(serverWorld, TE.getItemStack(), E), 0, 127)
                        : 0;
                if (i>0) {
                    TE.addCommandTag("void");
                    TE.setVelocity(0, 0, 0);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "isBeingRainedOn", at = @At(value = "HEAD"),cancellable = true)
    private void wetLingeringEffect(CallbackInfoReturnable<Boolean> cir) {
        Entity E = (Entity) (Object) this;
        if (E instanceof PlayerEntity PE) {
            for (AreaEffectCloudEntity effectCloud : PE.getWorld().getNonSpectatingEntities(AreaEffectCloudEntity.class, PE.getBoundingBox())) {
                if (effectCloud.getParticleType().getType() == ParticleTypes.SPLASH) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
