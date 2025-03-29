package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "tickMovement", at= @At("HEAD"))
    private void horsesSwimInWater(CallbackInfo ci) {
        LivingEntity LE = (LivingEntity) (Object)this;
        if (LE instanceof AbstractHorseEntity && LE.hasControllingPassenger() && LE.shouldDismountUnderwater()) {
            BlockPos blockPos = BlockPos.ofFloored(LE.getX(), LE.getY()+1, LE.getZ());
            FluidState fluidState = LE.getWorld().getFluidState(blockPos);
            if (fluidState.isIn(FluidTags.WATER)) {
                if (LE.getRandom().nextFloat() < 0.8F) {
                    LE.setVelocity(LE.getVelocity().add(0.0, 0.04F, 0.0));
                }
            }
        }
    }

    @Inject(method = "canUseSlot", at = @At(value = "HEAD"), cancellable = true)
    private void muleArmourslot(EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir){
        MobEntity LE = (MobEntity) (Object)this;
        if (LE instanceof MuleEntity) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
