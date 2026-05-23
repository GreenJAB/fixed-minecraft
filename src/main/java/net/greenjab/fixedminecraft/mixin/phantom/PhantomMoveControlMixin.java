package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.world.entity.monster.Phantom$PhantomMoveControl")
public abstract class PhantomMoveControlMixin {

    @Shadow
    @Final
    Phantom this$0;

    @ModifyVariable(method = "tick", at = @At(value = "STORE"), ordinal = 4)
    private double facePlayer(double a){
        return Math.max(a, 0.01);
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 4.0f))
    private float dontTurnAtHighSpeed(float constant){
        Phantom PE = this.this$0;
        LivingEntity Player = PE.level().getNearestPlayer(PE, 10);
        if (Player != null) {
            if (PE.getDeltaMovement().horizontalDistance() > 0.3) {
                return 0;
            }
        }
        return constant;
    }
}
