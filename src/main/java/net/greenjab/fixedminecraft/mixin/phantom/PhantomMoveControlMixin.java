package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$PhantomMoveControl")
class PhantomMoveControlMixin {

    @Shadow
    @Final
    PhantomEntity field_7330;

    @ModifyVariable(method = "tick", at = @At(value = "STORE"), ordinal = 4)
    private double facePlayer(double a){
        return Math.max(a, 0.01);
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 4.0f))
    private float dontTurnAtHighSpeed(float constant){
        PhantomEntity PE = this.field_7330;
        LivingEntity Player = PE.getEntityWorld().getClosestPlayer(PE, 10);
        if (Player != null) {
            if (PE.getVelocity().horizontalLength() > 0.3) {
                return 0;
            }
        }
        return constant;
    }
}
