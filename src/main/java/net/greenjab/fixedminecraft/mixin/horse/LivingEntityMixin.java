package net.greenjab.fixedminecraft.mixin.horse;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.MuleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @ModifyConstant(method = "getAttackDistanceScalingFactor",constant = @Constant(doubleValue = 0.8))
    private double moreSneaky(double constant){
        LivingEntity LE = (LivingEntity) (Object)this;
        if (LE instanceof HostileEntity) return 0.3;
        return constant;
    }
    @ModifyConstant(method = "getAttackDistanceScalingFactor",constant = @Constant(doubleValue = 1.0))
    private double zombieHorseSneaky(double constant){
        LivingEntity LE = (LivingEntity) (Object)this;
        Entity vehicle = LE.getVehicle();
        if (vehicle instanceof ZombieHorseEntity) return 0.5;
        return 1.0;
    }

    @ModifyExpressionValue(method = "canEquip", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/EquippableComponent;allows(Lnet/minecraft/entity/EntityType;)Z"))
    private boolean muleArmourEquipable(boolean original, @Local EquippableComponent equippableComponent){
        LivingEntity LE = (LivingEntity) (Object)this;
        if (LE instanceof MuleEntity) {
            if (equippableComponent.allows(EntityType.HORSE)) return true;
        }
        return original;
    }
}
