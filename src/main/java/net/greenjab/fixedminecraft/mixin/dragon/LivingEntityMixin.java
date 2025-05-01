package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 5))
    private boolean dontSlowdownEnderdragon(DamageSource source, TagKey<DamageType> tag) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof EnderDragonEntity || source.getAttacker() instanceof PhantomEntity) {
            return true;
        } else {
            return source.isIn(tag);
        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;becomeAngry(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void aggroEndermenToPlayer(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof EndermiteEntity) {
            Entity entity = source.getAttacker();
            if (entity!=null) {
                if (entity instanceof EndermanEntity endermanEntity){
                    LivingEntity livingEntity = endermanEntity.getWorld().getClosestPlayer(
                            endermanEntity.getX(), endermanEntity.getY(), endermanEntity.getZ(), 100.0,true);
                    endermanEntity.setTarget(livingEntity);
                    endermanEntity.setAngerTime(999999);
                }
            }
        }
    }
}
