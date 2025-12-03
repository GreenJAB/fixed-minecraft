package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.MuleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{

    @ModifyConstant(method = "getAttackDistanceScalingFactor",constant = @Constant(doubleValue = 1.0))
    private double zombieHorseSneaky(double constant){
        LivingEntity LE = (LivingEntity) (Object)this;
        Entity vehicle = LE.getVehicle();
        if (vehicle instanceof ZombieHorseEntity) return 0.5;
        return 1.0;
    }

    @Inject(method = "canUseSlot", at = @At(value = "HEAD"), cancellable = true)
    private void muleArmourslot(EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir){
        LivingEntity LE = (LivingEntity) (Object)this;
        if (LE instanceof MuleEntity) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
