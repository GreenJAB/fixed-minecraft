package net.greenjab.fixedminecraft.mixin.wolves;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FollowOwnerGoal.class)
public class FollowOwnerGoalMixin {
    @Shadow @Final private TameableEntity tameable;

    @Shadow private LivingEntity owner;

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/TameableEntity;squaredDistanceTo(Lnet/minecraft/entity/Entity;)D"), method = "tick")
    public double reduceTeleport(TameableEntity instance, Entity entity, Operation<Double> original) {
        // wolves will teleport if more than 12 blocks away
        // reduction is done if the wolf (or cat/parrot, but they dont attack) is attacking the same thing as their owner
        // or if they attacked in the last 10 seconds, which helps keep them on their target
        double distance = original.call(instance, entity);
        LivingEntity attacking = this.tameable.getAttacking();
        if (attacking != null && (attacking == this.owner.getAttacking() || (this.tameable.age-this.tameable.getLastAttackTime() < 200))) {
            //multiply effective distance by 6, from 12 to 72, this is a slightly silly way of doing this
            double x = Math.sqrt(distance) / 6.0;
            return x*x;
        }
        return distance;
    }
}
