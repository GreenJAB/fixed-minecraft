package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin {

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void dontHitItself(CallbackInfo ci, @Local(argsOnly = true) HitResult hitResult) {
        if (hitResult.getType()==HitResult.Type.ENTITY) {
            if (((EntityHitResult) hitResult).getEntity().getType() == EntityTypes.ENDER_DRAGON) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onHit", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;<init>(Lnet/minecraft/world/level/Level;DDD)V"))
    private void explodeOnImpact(HitResult hitResult, CallbackInfo ci) {
        if (!FixedMinecraft.gameRules.modified_dragon) return;
        DragonFireball DFE = (DragonFireball)(Object)this;
        ServerLevel level = (ServerLevel) DFE.level();
        int explosionPower = (DFE.level().getDifficulty().getId()+1)/2;
        boolean fire = false;
        if (DFE.getOwner()!=null) {
            if (DFE.getOwner().entityTags().contains("omen")) {
                explosionPower++;
                fire = true;
            }
        }
        assert level != null;
        level.explode(
                DFE,
                Explosion.getDefaultDamageSource(level, DFE),
                null,
                DFE.getX(), DFE.getY(), DFE.getZ(),
                explosionPower,
                fire,
                Level.ExplosionInteraction.NONE,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                WeightedList.<ExplosionParticleInfo>builder()
                        .add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
                        .add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
                        .build(),
                SoundEvents.GENERIC_EXPLODE
        );
    }

    @ModifyConstant(method = "onHit", constant = @Constant(floatValue = 7.0f))
    private float shrinkOverTime(float constant) {
        return -1;
    }
}
