package net.greenjab.fixedminecraft.mixin.horse;


import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @ModifyConstant(method = "addSoulSpeedBoostIfNeeded", constant = @Constant(floatValue = 0.03f))
    private float fasterHorseSoulSpeed(float constant) {
        LivingEntity LE = (LivingEntity) (Object)this;
        if (LE instanceof AbstractHorseEntity) {
            return constant*3;
        }
        return constant;
    }

    @ModifyConstant(method = "getAttackDistanceScalingFactor",constant = @Constant(doubleValue = 0.8))
    private double moreSneaky(double constant){
        return 0.3;
    }
    @ModifyConstant(method = "getAttackDistanceScalingFactor",constant = @Constant(doubleValue = 1.0))
    private double zombieHorseSneaky(double constant){
        LivingEntity LE = (LivingEntity) (Object)this;
        Entity vehicle = LE.getVehicle();
        if (vehicle instanceof ZombieHorseEntity) return 0.5;
        return 1.0;
    }
}
