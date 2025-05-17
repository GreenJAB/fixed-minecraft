package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractSittingPhase;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.boss.dragon.phase.SittingFlamingPhase;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SittingFlamingPhase.class)
public class SittingFlamingPhaseMixin extends AbstractSittingPhase {
    @Shadow
    private int ticks;

    @Shadow
    private int timesRun;

    public SittingFlamingPhaseMixin(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Inject(method = "serverTick", at = @At("HEAD"),cancellable = true)
    private void redoTick(CallbackInfo ci) {

        this.ticks++;

        TargetPredicate CLOSE_PLAYER_PREDICATE;
        CLOSE_PLAYER_PREDICATE = TargetPredicate.createAttackable()
                .setBaseMaxDistance(150.0)
                .setPredicate(/* method_18447 */ player -> Math.abs(player.getY() - this.dragon.getY()) <= 10.0);

        LivingEntity livingEntity = this.dragon
                .getWorld()
                .getClosestPlayer(CLOSE_PLAYER_PREDICATE, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());

        if (this.ticks >= 100) {
            if (this.dragon.getRandom().nextFloat()<((this.timesRun-1)/(this.timesRun+1.0f))) {

                livingEntity = this.dragon
                        .getWorld()
                        .getClosestPlayer(TargetPredicate.createAttackable().setBaseMaxDistance(150.0), this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
                this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
                if (livingEntity != null) {
                    if (livingEntity.squaredDistanceTo(this.dragon)>10*10) {
                        this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                        this.dragon.getPhaseManager()
                                .create(PhaseType.CHARGING_PLAYER)
                                .setPathTarget(new Vec3d(livingEntity.getX(), livingEntity.getY() - 1, livingEntity.getZ()));
                    }
                }
            } else {
                this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
            }
        } else if (this.ticks == 5) {
            double dp = 0;
            if (livingEntity!=null) {
                double dx = this.dragon.getX() - livingEntity.getX();
                double dz = this.dragon.getZ() - livingEntity.getZ();
                dp = Math.sqrt(dx * dx + dz * dz);
            }
            if (dp>25 && dp < 60) {
                Vec3d vec3d3 = this.dragon.getRotationVec(1.0F);
                double l = this.dragon.head.getX() - vec3d3.x;
                double m = this.dragon.head.getBodyY(0.5) + 0.5;
                double n = this.dragon.head.getZ() - vec3d3.z;
                double o = livingEntity.getX() - l;
                double p = livingEntity.getBodyY(0.5) - m;
                double q = livingEntity.getZ() - n;
                if (!this.dragon.isSilent()) {
                    this.dragon.getWorld().syncWorldEvent(null, WorldEvents.ENDER_DRAGON_SHOOTS, this.dragon.getBlockPos(), 0);
                }

                DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(this.dragon.getWorld(), this.dragon, new Vec3d(o, p, q));
                dragonFireballEntity.refreshPositionAndAngles(l, m, n, 0.0F, 0.0F);
                this.dragon.getWorld().spawnEntity(dragonFireballEntity);

            } else {

                Vec3d vec3d = new Vec3d(
                        this.dragon.head.getX() - this.dragon.getX(), 0.0, this.dragon.head.getZ() - this.dragon.getZ()).normalize();
                double d = this.dragon.head.getX() + vec3d.x * 5.0 / 2.0;
                double e = this.dragon.head.getZ() + vec3d.z * 5.0 / 2.0;
                double g = this.dragon.head.getBodyY(0.5);
                double h = g;
                BlockPos.Mutable mutable = new BlockPos.Mutable(d, g, e);

                while (this.dragon.getWorld().isAir(mutable)) {
                    if (--h < 0.0) {
                        h = g;
                        break;
                    }

                    mutable.set(d, h, e);
                }

                if (this.dragon.getCommandTags().contains("omen")) {
                    List<Entity> entities = this.dragon.getWorld()
                            .getOtherEntities(this.dragon, this.dragon.head.getBoundingBox().expand(2.0, 3.0, 2.0).offset(0.0, -1.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
                    for (Entity ee : entities) {
                        ee.setFireTicks(300);
                    }
                }

                h = (MathHelper.floor(h) + 1);
                AreaEffectCloudEntity areaEffectCloudEntity = dragonAreaEffectCloudEntity(d, h, e);
                areaEffectCloudEntity.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1));

                this.dragon.getWorld().syncWorldEvent(WorldEvents.DRAGON_BREATH_CLOUD_SPAWNS, this.dragon.getBlockPos(), this.dragon.isSilent() ? -1 : 1);
                this.dragon.getWorld().spawnEntity(areaEffectCloudEntity);
            }
        } else if (this.ticks >= 30) {

            if (livingEntity != null) {
                Vec3d vec3d = new Vec3d(
                        livingEntity.getX() - this.dragon.getX(), 0.0, livingEntity.getZ() - this.dragon.getZ()).normalize();
                Vec3d vec3d2 = new Vec3d(
                        MathHelper.sin(this.dragon.getYaw() * (float) (Math.PI / 180.0)),
                        0.0,
                        (-MathHelper.cos(this.dragon.getYaw() * (float) (Math.PI / 180.0)))
                )
                        .normalize();
                float f = (float) vec3d2.dotProduct(vec3d);
                float g = (float) (Math.acos(f) * 180.0F / (float) Math.PI);

                if (g < -5.0F || g > 5.0F) {
                    double d = livingEntity.getX() - this.dragon.getX();
                    double e = livingEntity.getZ() - this.dragon.getZ();
                    double h = MathHelper.clamp(MathHelper.wrapDegrees(
                            180.0 - MathHelper.atan2(d, e) * 180.0F / (float) Math.PI - (double) this.dragon.getYaw()), -20.0, 20.0);
                    this.dragon.yawAcceleration *= 0.8F;
                    float i = (float) Math.sqrt(d * d + e * e) + 1.0F;
                    float j = i;
                    if (i > 40.0F) {
                        i = 40.0F;
                    }

                    this.dragon.yawAcceleration += (float) h * (0.05F / (i / j));
                    this.dragon.setYaw(this.dragon.getYaw() + this.dragon.yawAcceleration);

                }
            }
        }
        ci.cancel();
    }


    @Unique
    @NotNull
    private AreaEffectCloudEntity dragonAreaEffectCloudEntity(double d, double h, double e) {
        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.dragon.getWorld(), d, h, e);
        areaEffectCloudEntity.setOwner(this.dragon);
        areaEffectCloudEntity.setParticleType(ParticleTypes.DRAGON_BREATH);
        areaEffectCloudEntity.setRadius(5.0F);
        areaEffectCloudEntity.setDuration(300);
        areaEffectCloudEntity.setRadiusGrowth((-0.5F - areaEffectCloudEntity.getRadius()) / (float)areaEffectCloudEntity.getDuration());
        return areaEffectCloudEntity;
    }

    @Override
    public PhaseType<? extends Phase> getType() {
        return PhaseType.SITTING_FLAMING;
    }
}
