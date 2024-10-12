package net.greenjab.fixedminecraft.mixin.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.ChargingPlayerPhase;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.boss.dragon.phase.StrafePlayerPhase;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChargingPlayerPhase.class)
public abstract class ChargingPlayerPhaseMixin extends AbstractPhase {

    @Shadow
    private @Nullable Vec3d pathTarget;

    @Shadow
    private int chargingTicks;

    public ChargingPlayerPhaseMixin(EnderDragonEntity dragon) {
        super(dragon);
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(DDD)D"))
    private void chaseElytraPlayer(CallbackInfo ci){
        boolean ischasing = false;
        PlayerEntity playerEntity = this.dragon.getWorld().getClosestPlayer(TargetPredicate.createAttackable().ignoreVisibility(), this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (playerEntity != null) {
            if (playerEntity.getPos().squaredDistanceTo(new Vec3d(0, 0, 0))<200*200) {
                if (playerEntity.isFallFlying()) {
                    this.pathTarget = playerEntity.getPos().add(playerEntity.getVelocity().multiply(5)).add(new Vec3d(0, -3, 0));
                    this.chargingTicks = 0;
                    ischasing = true;
                }
            } else {

                Vec3d vec3d3 = this.dragon.getRotationVec(1.0F);
                double l = this.dragon.head.getX() - vec3d3.x;
                double m = this.dragon.head.getBodyY(0.5) + 0.5;
                double n = this.dragon.head.getZ() - vec3d3.z;
                double o = playerEntity.getX() - l;
                double p = playerEntity.getBodyY(0.5) - m;
                double q = playerEntity.getZ() - n;

                if (!this.dragon.isSilent()) {
                    this.dragon.getWorld()
                            .syncWorldEvent(null, WorldEvents.ENDER_DRAGON_SHOOTS, this.dragon.getBlockPos(), 0);
                }
                DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(this.dragon.getWorld(), this.dragon, o, p, q);
                dragonFireballEntity.refreshPositionAndAngles(l, m, n, 0.0F, 0.0F);
                this.dragon.getWorld().spawnEntity(dragonFireballEntity);

                this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);

            }
        }
        List<Entity> entities = this.dragon.getWorld().getOtherEntities(this.dragon, this.dragon.getBoundingBox().expand(ischasing?1:-1).offset(0, -3, 0));
        for (Entity e : entities) {
            if (e instanceof LivingEntity player) {
                double f = player.getX() - this.dragon.getX();
                double g = player.getZ() - this.dragon.getZ();
                double h = Math.max(f * f + g * g, 0.1);
                //player.addVelocity(f / h * 4.0, 1.0, g / h * 4.0);
                player.addVelocity((f / h * 2.0)+this.dragon.getVelocity().getX()*10, 1, (g / h * 2.0)+this.dragon.getVelocity().getZ()*10);

                player.damage(this.dragon.getDamageSources().mobAttack(this.dragon), 5.0F);
                this.dragon.applyDamageEffects(this.dragon, player);
            }
        }
    }

    /*@Inject(method = "getMaxYAcceleration", at = @At(value = "HEAD"), cancellable = true)
    private void moreY(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(10f);
        cir.cancel();
    }*/

    @Override
    public PhaseType<? extends Phase> getType() {
        return PhaseType.STRAFE_PLAYER;
    }
}
