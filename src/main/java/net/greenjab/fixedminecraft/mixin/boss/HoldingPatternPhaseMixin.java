package net.greenjab.fixedminecraft.mixin.boss;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.HoldingPatternPhase;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(HoldingPatternPhase.class)
public class HoldingPatternPhaseMixin extends AbstractPhase {


    public HoldingPatternPhaseMixin(EnderDragonEntity dragon) {
        super(dragon);
    }

    @ModifyConstant(method = "tickInRange", constant = @Constant(intValue = 2))
    private int moreAttacks(int constant, @Local int i){
        return 1-i+(6-2*this.dragon.getWorld().getDifficulty().getId());
    }


    @ModifyArg(method = "followPath", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"), index = 1)
    private double lessYRange(double x, @Local Vec3i vec3i){
        Random random = new Random();
        return vec3i.getY() + ((random.nextGaussian()/4.0)+0.5)*20;
    }

    @Inject(method = "strafePlayer", at = @At(value = "HEAD"), cancellable = true)
    private void chargeAtPlayer(CallbackInfo ci, @Local PlayerEntity player) {
        if (player.getPos().squaredDistanceTo(new Vec3d(0, 0, 0))>150*150) {
            ci.cancel();
        } else {
            if (this.dragon.getRandom().nextInt(3) == 0 || player.isFallFlying()) {
                this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                this.dragon.getPhaseManager()
                        .create(PhaseType.CHARGING_PLAYER)
                        .setPathTarget(new Vec3d(player.getX(), player.getY()-1, player.getZ()));
                ci.cancel();
            }
        }
    }

    @Override
    public PhaseType<? extends Phase> getType() {
        return PhaseType.HOLDING_PATTERN;
    }
}
