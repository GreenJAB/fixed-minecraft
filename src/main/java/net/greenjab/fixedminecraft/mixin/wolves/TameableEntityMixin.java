package net.greenjab.fixedminecraft.mixin.wolves;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TamableAnimal.class)
public abstract class TameableEntityMixin {

    @WrapOperation( method = "shouldTryTeleportToOwner", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D"))
    public double reduceTeleport(TamableAnimal instance, Entity entity, Operation<Double> original) {
        // wolves will teleport if more than 12 blocks away
        // reduction is done if the wolf (or cat/parrot, but they don't attack) is attacking the same thing as their owner
        // or if they attacked in the last 10 seconds, which helps keep them on their target
        double distance = original.call(instance, entity);
        if ((TamableAnimal)(Object)this instanceof Wolf WE) {
            LivingEntity attacking = WE.getLastHurtMob();
            if (attacking != null &&
                (attacking == WE.getOwner().getLastHurtMob() || (WE.tickCount - WE.getLastHurtMobTimestamp() < 200))) {
                //multiply effective distance by 6, from 12 to 72, this is a slightly silly way of doing this
                double x = Math.sqrt(distance) / 6.0;
                return x * x;
            }
        }
        return distance;
    }
}
