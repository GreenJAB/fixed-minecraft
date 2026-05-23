package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonStrafePlayerPhase.class)
public abstract class DragonStrafePlayerPhaseMixin extends AbstractDragonPhaseInstance {

    @Shadow
    private @Nullable LivingEntity attackTarget;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private @Nullable Path currentPath;

    @Shadow
    private @Nullable Vec3 targetLocation;

    @Shadow
    protected abstract void findNewTarget();

    @Shadow
    private int fireballCharge;

    public DragonStrafePlayerPhaseMixin(EnderDragon dragon) {
        super(dragon);
    }

    @Inject(method = "doServerTick", at = @At(value = "HEAD"), cancellable = true)
    private void checkForDragonFight(CallbackInfo ci) {
        if (this.attackTarget == null || this.fireballCharge < -100) {
            LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
        } else {
            if (this.currentPath != null && this.currentPath.isDone()) {
                double d = this.attackTarget.getX();
                double e = this.attackTarget.getZ();
                double f = d - this.dragon.getX();
                double g = e - this.dragon.getZ();
                double h = Math.sqrt(f * f + g * g);
                double i = Math.min(0.4F + h / 80.0 - 1.0, 10.0);
                this.targetLocation = new Vec3(d, this.attackTarget.getY() + i, e);
            }

            double d = this.targetLocation == null ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            if (d < 100.0 || d > 22500.0) {
                this.findNewTarget();
            }

            if (this.attackTarget.distanceToSqr(this.dragon) < 22500.0) {
                if (this.dragon.hasLineOfSight(this.attackTarget)) {
                    this.fireballCharge++;

                    Vec3 vec3d = new Vec3(this.attackTarget.getX() - this.dragon.getX(), 0.0, this.attackTarget.getZ() - this.dragon.getZ()).normalize();
                    Vec3 vec3d2 = new Vec3(
                            Mth.sin(this.dragon.getYRot() * (float) (Math.PI / 180.0)),
                            0.0,
                            (-Mth.cos(this.dragon.getYRot() * (float) (Math.PI / 180.0)))
                    )
                            .normalize();
                    Vec3 vec3d3 = this.dragon.getViewVector(1.0F);

                    if (this.fireballCharge >= 5 ){

                        double dx = this.dragon.getX() - this.attackTarget.getX();
                        double dz = this.dragon.getZ() - this.attackTarget.getZ();

                        if (dx*dx+dz*dz < 16*16 && this.dragon.level().getDifficulty().getId()>1) {
                            if (!this.dragon.isSilent()) {
                                this.dragon.level()
                                        .levelEvent(null, LevelEvent.SOUND_DRAGON_FIREBALL, this.dragon.blockPosition(), 0);
                            }
                            BlockPos b = this.dragon.getSubEntities()[5].blockPosition();
                            Endermite endermiteEntity = EntityType.ENDERMITE.create(this.dragon.level().getChunkAt(b).getLevel(), EntitySpawnReason.MOB_SUMMONED);
                            if (endermiteEntity != null) {
                                endermiteEntity.snapTo(b.getX()-this.dragon.getDeltaMovement().x(), b.above().getY(), b.getZ()-this.dragon.getDeltaMovement().z(), 0, 0.0F);
                                endermiteEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, -1));
                                endermiteEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1));
                                endermiteEntity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 2, 4));
                                if (!this.dragon.entityTags().contains("omen")) {
                                    endermiteEntity.setHealth(1);
                                }
                                endermiteEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(30);
                                this.dragon.level().addFreshEntity(endermiteEntity);

                                this.fireballCharge = 0;
                                if (this.currentPath != null) {
                                    while (!this.currentPath.isDone()) {
                                        this.currentPath.advance();
                                    }
                                }

                                if (this.dragon.getRandom().nextInt(10)==0) {
                                    this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
                                    this.dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER).setTarget(new Vec3(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ()));
                                } else {
                                    this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
                                }
                            }
                        } else {
                            float j = (float)vec3d2.dot(vec3d);
                            float k = (float)(Math.acos(j) * 180.0F / (float)Math.PI);
                            if (k >= -45.0F && k < 45.0F) {

                                double l = this.dragon.head.getX() - vec3d3.x;
                                double m = this.dragon.head.getY(0.5) + 0.5;
                                double n = this.dragon.head.getZ() - vec3d3.z;
                                double o = this.attackTarget.getX() - l;
                                double p = this.attackTarget.getY(0.5) - m;
                                double q = this.attackTarget.getZ() - n;

                                if (!this.dragon.isSilent()) {
                                    this.dragon.level()
                                            .levelEvent(null, LevelEvent.SOUND_DRAGON_FIREBALL, this.dragon.blockPosition(), 0);
                                }
                                DragonFireball dragonFireballEntity = new DragonFireball(this.dragon.level(), this.dragon, new Vec3(o, p, q));
                                dragonFireballEntity.snapTo(l, m, n, 0.0F, 0.0F);
                                this.dragon.level().addFreshEntity(dragonFireballEntity);
                                this.fireballCharge = 0;
                                if (this.currentPath != null) {
                                    while (!this.currentPath.isDone()) {
                                        this.currentPath.advance();
                                    }
                                }

                                if (this.dragon.getRandom().nextBoolean()||this.dragon.level().getDifficulty().getId()<2) {
                                    this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
                                }if (this.dragon.getRandom().nextInt(10)==0) {
                                    this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
                                    this.dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER).setTarget(new Vec3(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ()));
                                } else {
                                    this.fireballCharge=-40;
                                }
                            }
                        }

                    }
                } else {
                    this.fireballCharge--;
                }
            } else {
                this.fireballCharge--;
            }
        }
        ci.cancel();
    }

    @Override
    public @NonNull EnderDragonPhase<? extends DragonPhaseInstance> getPhase() {
        return EnderDragonPhase.STRAFE_PLAYER;
    }
}
