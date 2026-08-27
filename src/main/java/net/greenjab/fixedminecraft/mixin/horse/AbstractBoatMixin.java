package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBoat.class)
public abstract class AbstractBoatMixin {
    @Inject(method = "hasEnoughSpaceFor", at = @At("HEAD"), cancellable = true)
    private void horseInBoat(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof AbstractHorse) {
            cir.setReturnValue(true);
        }
    }

    @WrapOperation(method = "tick", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hasPassenger(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean hostileNoGoIntoBoat(Entity instance, Entity entity, Operation<Boolean> original) {
        if (instance instanceof Monster monster){
            LivingEntity target = monster.getTarget();
            AbstractBoat boat = (AbstractBoat) (Object)this;
            if (target != null && !boat.hasPassenger(target)) return true;
        }
        return original.call(instance, entity);
    }

    @WrapOperation(method = "tick", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;getControllingPassenger()Lnet/minecraft/world/entity/LivingEntity;"))
    private LivingEntity hostileNoGoIntoBoat2(AbstractBoat instance, Operation<LivingEntity> original) {
        return null;
    }
}
