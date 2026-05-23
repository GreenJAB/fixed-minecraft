package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
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
    private void checkForDragonFight(CallbackInfo ci, @Local(argsOnly = true) HitResult hitResult) {
        if (hitResult.getType()==HitResult.Type.ENTITY) {
            if (((EntityHitResult) hitResult).getEntity().getType() == EntityType.ENDER_DRAGON) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onHit", at= @At(value = "INVOKE",
                                            target = "Lnet/minecraft/world/entity/AreaEffectCloud;<init>(Lnet/minecraft/world/level/Level;DDD)V"
    ))
    private void explodeOnImpact(HitResult hitResult, CallbackInfo ci) {
        DragonFireball DFE = (DragonFireball)(Object)this;
        ServerLevel world = (ServerLevel) DFE.level();
        int explosionPower = (DFE.level().getDifficulty().getId()+1)/2;
        if (DFE.getOwner()!=null) {
            if (DFE.getOwner().entityTags().contains("omen")) {
                explosionPower++;
            }
        }
        assert world != null;
        world.explode(
                DFE, DFE.getX(), DFE.getY(), DFE.getZ(), explosionPower,Level.ExplosionInteraction.NONE
        );
    }

    @ModifyConstant(method = "onHit", constant = @Constant(floatValue = 7.0f))
    private float shrinkOverTime(float constant) {
        return -1;
    }
}
