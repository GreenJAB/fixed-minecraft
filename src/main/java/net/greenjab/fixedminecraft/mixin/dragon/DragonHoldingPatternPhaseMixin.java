package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonHoldingPatternPhase.class)
public abstract class DragonHoldingPatternPhaseMixin extends AbstractDragonPhaseInstance {

    public DragonHoldingPatternPhaseMixin(EnderDragon dragon) {
        super(dragon);
    }

    @ModifyConstant(method = "findNewTarget", constant = @Constant(intValue = 2))
    private int moreAttacks(int constant, @Local int crystals){
        return 1 - crystals + (6 - 2 * this.dragon.level().getDifficulty().getId());
    }

    @ModifyArg(method = "navigateToNextPathNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"), index = 1)
    private double lessYRange(double x, @Local Vec3i current){
        return current.getY() + ((this.dragon.getRandom().nextGaussian() / 4.0) + 0.5) * 20;
    }

    @Inject(method = "strafePlayer", at = @At(value = "HEAD"), cancellable = true)
    private void chargeAtPlayer(CallbackInfo ci, @Local(argsOnly = true) Player playerNearestToEgg) {
        if (playerNearestToEgg.position().distanceToSqr(new Vec3(0, 0, 0)) > 150 * 150) {
            ci.cancel();
        } else {
            if (this.dragon.getRandom().nextInt(3) == 0 || playerNearestToEgg.isFallFlying()) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
                this.dragon.getPhaseManager()
                        .getPhase(EnderDragonPhase.CHARGING_PLAYER)
                        .setTarget(new Vec3(playerNearestToEgg.getX(), playerNearestToEgg.getY() - 1, playerNearestToEgg.getZ()));
                ci.cancel();
            }
        }
    }

    @Override
    public @NonNull EnderDragonPhase<? extends DragonPhaseInstance> getPhase() {
        return EnderDragonPhase.HOLDING_PATTERN;
    }
}
