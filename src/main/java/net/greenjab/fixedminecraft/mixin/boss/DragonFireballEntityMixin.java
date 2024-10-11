package net.greenjab.fixedminecraft.mixin.boss;

import com.ibm.icu.text.Edits;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(DragonFireballEntity.class)
public class DragonFireballEntityMixin {


    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void checkForDragonFight(CallbackInfo ci, @Local HitResult hitResult) {
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
        if (DFE.getOwner().getCommandTags().contains("omen")) {
            explosionPower++;
        }
        world.createExplosion(
                null, DFE.getX(), DFE.getY(), DFE.getZ(), explosionPower,World.ExplosionSourceType.NONE
        );
    }

    @ModifyConstant(method = "onCollision", constant = @Constant(floatValue = 7.0f))
    private float shrinkOverTime(float constant) {
        return -1;
    }
}
