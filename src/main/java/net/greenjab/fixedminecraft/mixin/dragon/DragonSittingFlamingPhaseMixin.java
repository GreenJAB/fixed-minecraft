package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingFlamingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;

@Mixin(DragonSittingFlamingPhase.class)
public abstract class DragonSittingFlamingPhaseMixin extends AbstractDragonSittingPhase {
    @Shadow
    private int flameTicks;

    @Shadow
    private int flameCount;

    public DragonSittingFlamingPhaseMixin(EnderDragon enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Inject(method = "doServerTick", at = @At("HEAD"),cancellable = true)
    private void redoTick(CallbackInfo ci, @Local(argsOnly = true) ServerLevel level) {

        this.flameTicks++;

        TargetingConditions CLOSE_PLAYER_PREDICATE;
        CLOSE_PLAYER_PREDICATE = TargetingConditions.forCombat()
                .range(150.0)
                .selector(/* method_18447 */ (player, _) -> Math.abs(player.getY() - this.dragon.getY()) <= 10.0);

        LivingEntity livingEntity = level
                .getNearestPlayer(CLOSE_PLAYER_PREDICATE, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());

        if (this.flameTicks >= 100) {
            if (this.dragon.getRandom().nextFloat()<((this.flameCount-1)/(this.flameCount+1.0f))) {

                livingEntity = level.getNearestPlayer(TargetingConditions.forCombat().range(150.0), this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
                if (livingEntity != null) {
                    if (livingEntity.distanceToSqr(this.dragon)>10*10) {
                        this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
                        this.dragon.getPhaseManager()
                                .getPhase(EnderDragonPhase.CHARGING_PLAYER)
                                .setTarget(new Vec3(livingEntity.getX(), livingEntity.getY() - 1, livingEntity.getZ()));
                    }
                }
            } else {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
            }
        } else if (this.flameTicks == 5) {
            double dp = 0;
            if (livingEntity!=null) {
                double dx = this.dragon.getX() - livingEntity.getX();
                double dz = this.dragon.getZ() - livingEntity.getZ();
                dp = Math.sqrt(dx * dx + dz * dz);
            }
            if (dp>25 && dp < 60) {
                Vec3 vec3d3 = this.dragon.getViewVector(1.0F);
                double l = this.dragon.head.getX() - vec3d3.x;
                double m = this.dragon.head.getY(0.5) + 0.5;
                double n = this.dragon.head.getZ() - vec3d3.z;
                double o = livingEntity.getX() - l;
                double p = livingEntity.getY(0.5) - m;
                double q = livingEntity.getZ() - n;
                if (!this.dragon.isSilent()) {
                    this.dragon.level().levelEvent(null, LevelEvent.SOUND_DRAGON_FIREBALL, this.dragon.blockPosition(), 0);
                }
                DragonFireball dragonFireballEntity = new DragonFireball(this.dragon.level(), this.dragon, new Vec3(o, p, q));
                dragonFireballEntity.snapTo(l, m, n, 0.0F, 0.0F);
                this.dragon.level().addFreshEntity(dragonFireballEntity);

            } else {

                Vec3 vec3d = new Vec3(
                        this.dragon.head.getX() - this.dragon.getX(), 0.0, this.dragon.head.getZ() - this.dragon.getZ()).normalize();
                double d = this.dragon.head.getX() + vec3d.x * 5.0 / 2.0;
                double e = this.dragon.head.getZ() + vec3d.z * 5.0 / 2.0;
                double g = this.dragon.head.getY(0.5);
                double h = g;
                BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(d, g, e);

                while (this.dragon.level().isEmptyBlock(mutable)) {
                    if (--h < 0.0) {
                        h = g;
                        break;
                    }

                    mutable.set(d, h, e);
                }

                if (this.dragon.entityTags().contains("omen")) {
                    List<Entity> entities = this.dragon.level()
                            .getEntities(this.dragon, this.dragon.head.getBoundingBox().inflate(2.0, 3.0, 2.0).move(0.0, -1.0, 0.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
                    for (Entity ee : entities) {
                        ee.setRemainingFireTicks(300);
                    }
                }

                h = (Mth.floor(h) + 1);
                AreaEffectCloud areaEffectCloudEntity = dragonAreaEffectCloudEntity(d, h, e);
                areaEffectCloudEntity.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1));

                this.dragon.level().levelEvent(LevelEvent.PARTICLES_DRAGON_FIREBALL_SPLASH, this.dragon.blockPosition(), this.dragon.isSilent() ? -1 : 1);
                this.dragon.level().addFreshEntity(areaEffectCloudEntity);
            }
        } else if (this.flameTicks >= 30) {

            if (livingEntity != null) {
                Vec3 vec3d = new Vec3(
                        livingEntity.getX() - this.dragon.getX(), 0.0, livingEntity.getZ() - this.dragon.getZ()).normalize();
                Vec3 vec3d2 = new Vec3(
                         Mth.sin(this.dragon.getYRot() * (float) (Math.PI / 180.0)),
                        0.0,
                         (-Mth.cos(this.dragon.getYRot() * (float) (Math.PI / 180.0)))
                )
                        .normalize();
                float f = (float) vec3d2.dot(vec3d);
                float g = (float) (Math.acos(f) * 180.0F / (float) Math.PI);

                if (g < -5.0F || g > 5.0F) {
                    double d = livingEntity.getX() - this.dragon.getX();
                    double e = livingEntity.getZ() - this.dragon.getZ();
                    double h = Mth.clamp(Mth.wrapDegrees(
                            180.0 - Mth.atan2(d, e) * 180.0F / (float) Math.PI - (double) this.dragon.getYRot()), -20.0, 20.0);
                    this.dragon.yRotA *= 0.8F;
                    float i = (float) Math.sqrt(d * d + e * e) + 1.0F;
                    float j = i;
                    if (i > 40.0F) {
                        i = 40.0F;
                    }

                    this.dragon.yRotA += (float) h * (0.05F / (i / j));
                    this.dragon.setYRot(this.dragon.getYRot() + this.dragon.yRotA);

                }
            }
        }
        ci.cancel();
    }

    @Unique
    @NotNull
    private AreaEffectCloud dragonAreaEffectCloudEntity(double d, double h, double e) {
        AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(this.dragon.level(), d, h, e);
        areaEffectCloudEntity.setOwner(this.dragon);
        areaEffectCloudEntity.setCustomParticle(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F));
        areaEffectCloudEntity.setRadius(5.0F);
        areaEffectCloudEntity.setDuration(300);
        areaEffectCloudEntity.setRadiusPerTick((-0.5F - areaEffectCloudEntity.getRadius()) / (float)areaEffectCloudEntity.getDuration());
        return areaEffectCloudEntity;
    }

    @Override
    public @NonNull EnderDragonPhase<? extends DragonPhaseInstance> getPhase() {
        return EnderDragonPhase.SITTING_FLAMING;
    }
}
