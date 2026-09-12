package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TargetGoal.class)
public abstract class TargetGoalMixin {

    @Shadow @Final protected Mob mob;

    @WrapOperation(method = "canContinueToUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/target/TargetGoal;getFollowDistance()D"))
    private double stopFollowingLowVisibility(TargetGoal instance, Operation<Double> original, @Local LivingEntity target){
       return original.call(instance) * Math.min(target.getVisibilityPercent((ServerLevel) mob.level(), mob) * 2, 1);
   }
}
