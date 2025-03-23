package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            double d = LE.getY();
            BlockPos blockPos = BlockPos.ofFloored(LE.getX(), d, LE.getZ());
            FluidState fluidState = LE.getWorld().getFluidState(blockPos);
            if (fluidState.isIn(FluidTags.WATER) && isFluidAboveEqual(fluidState, LE.getWorld(), blockPos)) {
                if (LE.getRandom().nextFloat() < 0.8F) {
                    LE.setVelocity(LE.getVelocity().add(0.0, 0.04F, 0.0));
                }
            }
        }
    }

    @Unique
    private static boolean isFluidAboveEqual(FluidState state, BlockView world, BlockPos pos) {
        return state.getFluid().matchesType(world.getFluidState(pos.up()).getFluid());
    }
}
