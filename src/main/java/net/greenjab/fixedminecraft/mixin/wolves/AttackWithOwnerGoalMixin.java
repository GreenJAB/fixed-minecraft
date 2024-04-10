package net.greenjab.fixedminecraft.mixin.wolves;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttackWithOwnerGoal.class)
public abstract class AttackWithOwnerGoalMixin extends TrackTargetGoal {
    public AttackWithOwnerGoalMixin(MobEntity mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }

    @WrapOperation(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/AttackWithOwnerGoal;canTrack(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/ai/TargetPredicate;)Z"))
    private boolean alwaysAllow(AttackWithOwnerGoal instance, LivingEntity livingEntity, TargetPredicate targetPredicate, Operation<Boolean> original) {
        return true;
    }

    @Override
    protected double getFollowRange() {
        return super.getFollowRange()*4;
    }
}
