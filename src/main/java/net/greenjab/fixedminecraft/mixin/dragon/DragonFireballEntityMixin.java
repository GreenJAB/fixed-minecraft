package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireballEntity.class)
public class DragonFireballEntityMixin {


    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void checkForDragonFight(CallbackInfo ci, @Local(argsOnly = true) HitResult hitResult) {
        if (hitResult.getType()==HitResult.Type.ENTITY) {
            if (((EntityHitResult) hitResult).getEntity().getType() == EntityType.ENDER_DRAGON) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onCollision", at= @At(value = "INVOKE",
                                            target = "Lnet/minecraft/entity/AreaEffectCloudEntity;<init>(Lnet/minecraft/world/World;DDD)V"
    ))
    private void explodeOnImpact(HitResult hitResult, CallbackInfo ci) {

        DragonFireballEntity DFE = (DragonFireballEntity)(Object)this;
        ServerWorld world = DFE.getServer().getWorld(DFE.getWorld().getRegistryKey());
        int explosionPower = (DFE.getWorld().getDifficulty().getId()+1)/2;
        if (DFE.getOwner()!=null) {
            if (DFE.getOwner().getCommandTags().contains("omen")) {
                explosionPower++;
            }
        }
        assert world != null;
        world.createExplosion(
                DFE, DFE.getX(), DFE.getY(), DFE.getZ(), explosionPower,World.ExplosionSourceType.NONE
        );
    }

    @ModifyConstant(method = "onCollision", constant = @Constant(floatValue = 7.0f))
    private float shrinkOverTime(float constant) {
        return -1;
    }
}
