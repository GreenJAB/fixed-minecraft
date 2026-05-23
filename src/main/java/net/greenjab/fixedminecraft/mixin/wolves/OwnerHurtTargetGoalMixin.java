package net.greenjab.fixedminecraft.mixin.wolves;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Credit:Nettakrim */
@Mixin(OwnerHurtTargetGoal.class)
public abstract class OwnerHurtTargetGoalMixin extends TargetGoal {
    public OwnerHurtTargetGoalMixin(Mob mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }

    @WrapOperation(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/target/OwnerHurtTargetGoal;canAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;)Z"))
    private boolean alwaysAllow(OwnerHurtTargetGoal instance, LivingEntity livingEntity, TargetingConditions targetPredicate, Operation<Boolean> original) {
        return true;
    }

    @Override
    protected double getFollowDistance() {
        return super.getFollowDistance()*4;
    }
}
