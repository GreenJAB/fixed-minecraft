package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingScanningPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonSittingScanningPhase.class)
public abstract class DragonSittingScanningPhaseMixin extends AbstractDragonSittingPhase {
    @Shadow
    private int scanningTime;

    @Shadow
    @Final
    private TargetingConditions scanTargeting;

    @Shadow
    @Final
    private static TargetingConditions CHARGE_TARGETING;

    public DragonSittingScanningPhaseMixin(EnderDragon enderDragonEntity) {
        super(enderDragonEntity);
    }

    @ModifyConstant(method = "<init>", constant = @Constant(doubleValue = 20))
    private double longerSight(double constant){
        return 150;
    }

    @Inject(method = "doServerTick", at = @At("HEAD"),cancellable = true)
    private void redoTick(CallbackInfo ci, @Local(argsOnly = true) ServerLevel level) {
        this.scanningTime++;
        LivingEntity livingEntity = level.getNearestPlayer(this.scanTargeting, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (livingEntity != null) {
            if (this.scanningTime > 25) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_ATTACKING);
            } else {
                Vec3 vec3d = new Vec3(livingEntity.getX() - this.dragon.getX(), 0.0, livingEntity.getZ() - this.dragon.getZ()).normalize();
                Vec3 vec3d2 = new Vec3(
                         Mth.sin(this.dragon.getYRot() * (float) (Math.PI / 180.0)),
                        0.0,
                        (-Mth.cos(this.dragon.getYRot() * (float) (Math.PI / 180.0)))
                )
                        .normalize();
                float f = (float)vec3d2.dot(vec3d);
                float g = (float)(Math.acos(f) * 180.0F / (float)Math.PI);

                if (g < -5.0F || g > 5.0F) {
                    double d = livingEntity.getX() - this.dragon.getX();
                    double e = livingEntity.getZ() - this.dragon.getZ();
                    double h = Mth.clamp(Mth.wrapDegrees(180.0 - Mth.atan2(d, e) * 180.0F / (float)Math.PI - (double)this.dragon.getYRot()), -20.0, 20.0);
                    this.dragon.yRotA *= 0.8F;
                    float i = (float)Math.sqrt(d * d + e * e) + 1.0F;
                    float j = i;
                    if (i > 40.0F) {
                        i = 40.0F;
                    }

                    this.dragon.yRotA += (float)h * (0.05F / (i / j));
                    this.dragon.setYRot(this.dragon.getYRot() + this.dragon.yRotA);
                }


            }
        } else if (this.scanningTime >= 100) {
            livingEntity = level.getNearestPlayer(CHARGE_TARGETING, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
            if (livingEntity != null) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
                this.dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER).setTarget(new Vec3(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ()));
            }
        }
        ci.cancel();
    }

    @Override
    public @NonNull EnderDragonPhase<? extends DragonPhaseInstance> getPhase() {
        return EnderDragonPhase.SITTING_SCANNING;
    }
}
