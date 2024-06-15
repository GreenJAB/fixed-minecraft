package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin extends ThrownItemEntity {
    @Unique
    @Nullable
    private LivingEntity vehicle = null;

    public EnderPearlEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Saves the mounted entity to prevent silly teleportation exploit.
     * <br>
     * I.e. go to place A, sit in a boat and throw ender pearl into a bubble column.
     * Go to place B and mount a horse. Ask a friend or use redstone to trigger the ender pearl.
     * Voila, you have teleported your horse!
     */
    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)V", at = @At("TAIL"))
    private void saveVehicle(World world, LivingEntity owner, CallbackInfo ci) {
        vehicle = rootVehicle(owner);
    }

    /**
     * Teleports the player vehicle to the destination if it matches the saved one.
     * FIXME: Player controlled mobs don't teleport I DO NOT KNOW WHY PLEASE HELP ME
     */
    @WrapOperation(
            method = "onCollision", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;requestTeleportAndDismount(DDD)V"
    )
    )
    private void teleportWithVehicle(ServerPlayerEntity instance, double x, double y, double z, Operation<Void> original,
                                     @Share("passed")
                                     LocalBooleanRef ref) {
        LivingEntity currentVehicle = rootVehicle(instance);
        if (currentVehicle == null || !currentVehicle.equals(vehicle)) {
            original.call(instance, x, y, z);
            ref.set(false);
        }
        else {
            vehicle = currentVehicle;
            vehicle.requestTeleport(x, y, z);
            if (vehicle instanceof PathAwareEntity pathAwareEntity)
                pathAwareEntity.getNavigation().stop();
            ref.set(true);
        }
    }

    /**
     * Damages the saved vehicle and all passengers if the group teleportation was successful.
     */
    @WrapOperation(
            method = "onCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    )
    private boolean damagePassengers(Entity instance, DamageSource source, float amount, Operation<Boolean> original,
                                     @Share("passed")
                                     LocalBooleanRef ref) {
        if (!ref.get()) return original.call(instance, source, amount);
        assert vehicle != null;

        boolean damaged = false;
        for (Entity passenger : vehicle.getPassengersDeep())
            damaged |= original.call(passenger, source, amount);
        return damaged;
    }

    /**
     * Recursively gets the bottommost vehicle.
     */
    @Unique
    @Nullable
    private LivingEntity rootVehicle(LivingEntity entity) {
        if (!entity.hasVehicle()) return null;
        if (!(entity.getVehicle() instanceof LivingEntity veh)) return null;
        LivingEntity subVehicle = rootVehicle(veh);
        return subVehicle == null ? veh : subVehicle;
    }
}
