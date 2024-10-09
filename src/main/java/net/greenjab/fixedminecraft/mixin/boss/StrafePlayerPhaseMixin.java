package net.greenjab.fixedminecraft.mixin.boss;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.boss.dragon.phase.StrafePlayerPhase;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StrafePlayerPhase.class)
public abstract class StrafePlayerPhaseMixin extends AbstractPhase {


    /*@Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/pathing/Path;isFinished()Z", ordinal = 0))
    private void checkForDragonFight(CallbackInfo ci) {
        System.out.println("tick2");
    }*/

    @Shadow
    private @Nullable LivingEntity target;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private @Nullable Path path;

    @Shadow
    private @Nullable Vec3d pathTarget;

    @Shadow
    protected abstract void updatePath();

    @Shadow
    private int seenTargetTimes;

    public StrafePlayerPhaseMixin(EnderDragonEntity dragon) {
        super(dragon);
    }

    @Inject(method = "serverTick", at = @At(value = "HEAD"), cancellable = true)
    private void checkForDragonFight(CallbackInfo ci) {
        if (this.target == null) {
            this.LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
        } else {
            if (this.path != null && this.path.isFinished()) {
                double d = this.target.getX();
                double e = this.target.getZ();
                double f = d - this.dragon.getX();
                double g = e - this.dragon.getZ();
                double h = Math.sqrt(f * f + g * g);
                double i = Math.min(0.4F + h / 80.0 - 1.0, 10.0);
                this.pathTarget = new Vec3d(d, this.target.getY() + i, e);
            }

            double d = this.pathTarget == null ? 0.0 : this.pathTarget.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            if (d < 100.0 || d > 22500.0) {
                this.updatePath();
            }

            double e = 64.0;
            if (this.target.squaredDistanceTo(this.dragon) < 22500.0) {
                if (this.dragon.canSee(this.target)) {
                    this.seenTargetTimes++;

                    Vec3d vec3d = new Vec3d(this.target.getX() - this.dragon.getX(), 0.0, this.target.getZ() - this.dragon.getZ()).normalize();
                    Vec3d vec3d2 = new Vec3d(
                            (double) MathHelper.sin(this.dragon.getYaw() * (float) (Math.PI / 180.0)),
                            0.0,
                            (double)(-MathHelper.cos(this.dragon.getYaw() * (float) (Math.PI / 180.0)))
                    )
                            .normalize();
                    Vec3d vec3d3 = this.dragon.getRotationVec(1.0F);

                    if (this.seenTargetTimes >= 5 ){

                        double dx = this.dragon.getX() - this.target.getX();
                        double dz = this.dragon.getZ() - this.target.getZ();

                        if (dx*dx+dz*dz < 16*16 && this.dragon.getWorld().getDifficulty().getId()>1) {
                            if (!this.dragon.isSilent()) {
                                this.dragon.getWorld()
                                        .syncWorldEvent(null, WorldEvents.ENDER_DRAGON_SHOOTS, this.dragon.getBlockPos(), 0);
                            }
                            BlockPos b = this.dragon.getBodyParts()[5].getBlockPos();
                            EndermiteEntity endermiteEntity = EntityType.ENDERMITE.create(this.dragon.getWorld().getWorldChunk(b).getWorld());
                            if (endermiteEntity != null) {
                                endermiteEntity.refreshPositionAndAngles(b.getX()-this.dragon.getVelocity().getX(), b.up().getY(), b.getZ()-this.dragon.getVelocity().getY(), 0, 0.0F);
                                endermiteEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, -1));
                                endermiteEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, -1));
                                endermiteEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 4));
                                endermiteEntity.setHealth(1);
                                endermiteEntity.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(30);
                                this.dragon.getWorld().spawnEntity(endermiteEntity);

                                this.seenTargetTimes = 0;
                                if (this.path != null) {
                                    while (!this.path.isFinished()) {
                                        this.path.next();
                                    }
                                }

                                if (this.dragon.getRandom().nextInt(10)==0) {
                                    this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                                    this.dragon.getPhaseManager().create(PhaseType.CHARGING_PLAYER).setPathTarget(new Vec3d(this.target.getX(), this.target.getY(), this.target.getZ()));
                                } else {
                                    this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
                                }
                            }
                        } else {
                            float j = (float)vec3d2.dotProduct(vec3d);
                            float k = (float)(Math.acos((double)j) * 180.0F / (float)Math.PI);
                            //k += 0.5F;< -5.0F || g > 5.0F)
                            if (k >= -45.0F && k < 45.0F) {
                                double h = 1.0;

                                double l = this.dragon.head.getX() - vec3d3.x;
                                double m = this.dragon.head.getBodyY(0.5) + 0.5;
                                double n = this.dragon.head.getZ() - vec3d3.z;
                                double o = this.target.getX() - l;
                                double p = this.target.getBodyY(0.5) - m;
                                double q = this.target.getZ() - n;

                                if (!this.dragon.isSilent()) {
                                    this.dragon.getWorld()
                                            .syncWorldEvent(null, WorldEvents.ENDER_DRAGON_SHOOTS, this.dragon.getBlockPos(), 0);
                                }
                                DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(this.dragon.getWorld(), this.dragon, o, p, q);
                                dragonFireballEntity.refreshPositionAndAngles(l, m, n, 0.0F, 0.0F);
                                this.dragon.getWorld().spawnEntity(dragonFireballEntity);
                                this.seenTargetTimes = 0;
                                if (this.path != null) {
                                    while (!this.path.isFinished()) {
                                        this.path.next();
                                    }
                                }

                                if (this.dragon.getRandom().nextBoolean()||this.dragon.getWorld().getDifficulty().getId()<2) {
                                    this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
                                }if (this.dragon.getRandom().nextInt(10)==0) {
                                    this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                                    this.dragon.getPhaseManager().create(PhaseType.CHARGING_PLAYER).setPathTarget(new Vec3d(this.target.getX(), this.target.getY(), this.target.getZ()));
                                } else {
                                    this.seenTargetTimes=-40;
                                }
                            }
                        }

                    }
                } else if (this.seenTargetTimes > 0) {
                    this.seenTargetTimes--;
                }
            } else if (this.seenTargetTimes > 0) {
                this.seenTargetTimes--;
            }
        }
        ci.cancel();
    }

    @Override
    public PhaseType<? extends Phase> getType() {
        return PhaseType.STRAFE_PLAYER;
    }
}
