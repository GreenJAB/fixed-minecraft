package net.greenjab.fixedminecraft.mixin.phantom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.world.entity.monster.Phantom$PhantomSweepAttackGoal")
public abstract class PhantomSweepAttackGoalMixin {

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"), index = 0)
    private double smallerAttackHitbox(double constant){
        return -constant;
    }
}
