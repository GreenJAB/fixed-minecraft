package net.greenjab.fixedminecraft.mixin.phantom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$SwoopMovementGoal")
class PhantomSwoopMovementGoalMixin {

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"), index = 0)
    private double smallerAttackHitbox(double constant){
        return -constant;
    }
}
