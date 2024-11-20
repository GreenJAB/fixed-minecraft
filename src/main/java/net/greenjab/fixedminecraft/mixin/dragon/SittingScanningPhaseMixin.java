package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractSittingPhase;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.boss.dragon.phase.SittingScanningPhase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SittingScanningPhase.class)
public class SittingScanningPhaseMixin extends AbstractSittingPhase {
    @Shadow
    private int ticks;

    @Shadow
    @Final
    private TargetPredicate CLOSE_PLAYER_PREDICATE;

    @Shadow
    @Final
    private static TargetPredicate PLAYER_WITHIN_RANGE_PREDICATE;

    public SittingScanningPhaseMixin(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @ModifyConstant(method = "<init>", constant = @Constant(doubleValue = 20))
    private double longerSight(double constant){
        return 150;
    }

    @Inject(method = "serverTick", at = @At("HEAD"),cancellable = true)
    private void redoTick(CallbackInfo ci) {
        this.ticks++;
        LivingEntity livingEntity = this.dragon
                .getWorld()
                .getClosestPlayer(this.CLOSE_PLAYER_PREDICATE, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (livingEntity != null) {
            if (this.ticks > 25) {
                this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
            } else {
                Vec3d vec3d = new Vec3d(livingEntity.getX() - this.dragon.getX(), 0.0, livingEntity.getZ() - this.dragon.getZ()).normalize();
                Vec3d vec3d2 = new Vec3d(
                         MathHelper.sin(this.dragon.getYaw() * (float) (Math.PI / 180.0)),
                        0.0,
                        (-MathHelper.cos(this.dragon.getYaw() * (float) (Math.PI / 180.0)))
                )
                        .normalize();
                float f = (float)vec3d2.dotProduct(vec3d);
                float g = (float)(Math.acos(f) * 180.0F / (float)Math.PI);

                if (g < -5.0F || g > 5.0F) {
                    double d = livingEntity.getX() - this.dragon.getX();
                    double e = livingEntity.getZ() - this.dragon.getZ();
                    double h = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(d, e) * 180.0F / (float)Math.PI - (double)this.dragon.getYaw()), -20.0, 20.0);
                    this.dragon.yawAcceleration *= 0.8F;
                    float i = (float)Math.sqrt(d * d + e * e) + 1.0F;
                    float j = i;
                    if (i > 40.0F) {
                        i = 40.0F;
                    }

                    this.dragon.yawAcceleration += (float)h * (0.05F / (i / j));
                    this.dragon.setYaw(this.dragon.getYaw() + this.dragon.yawAcceleration);
                }


            }
        } else if (this.ticks >= 100) {
            livingEntity = this.dragon
                    .getWorld()
                    .getClosestPlayer(PLAYER_WITHIN_RANGE_PREDICATE, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
            if (livingEntity != null) {
                this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                this.dragon.getPhaseManager().create(PhaseType.CHARGING_PLAYER).setPathTarget(new Vec3d(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ()));
            }
        }
        ci.cancel();
    }

    @Override
    public PhaseType<? extends Phase> getType() {
        return PhaseType.SITTING_SCANNING;
    }
}
