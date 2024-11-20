package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

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
        if (owner.hasVehicle()) {
            if (owner == rootVehicle(owner).getControllingPassenger()) {
                vehicle = rootVehicle(owner);
            }
        }
    }

    /**
     * Teleports the player vehicle to the destination if it matches the saved one.
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
        Criteria.CONSUME_ITEM.trigger(instance, Items.ENDER_PEARL.getDefaultStack());
        LivingEntity currentVehicle = rootVehicle(instance);
        if (currentVehicle == null || !currentVehicle.equals(vehicle)) {
            original.call(instance, x, y, z);
            ref.set(false);
        }
        else {
            vehicle.requestTeleport(x, y, z);
            vehicle.addCommandTag("tp");

            if (vehicle instanceof PathAwareEntity pathAwareEntity)
                pathAwareEntity.getNavigation().stop();

            vehicle.onLanding();
            EnderPearlEntity EPE = (EnderPearlEntity) (Object) this;
            if (!((PlayerEntity) Objects.requireNonNull((EPE).getOwner())).getAbilities().creativeMode) {
                vehicle.damage(this.getDamageSources().fall(), 5.0F);
            }
            ref.set(true);
        }
    }

    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;requestTeleport(DDD)V", ordinal = 0))
    private void enderPearlAdvancement(HitResult hitResult, CallbackInfo ci, @Local(ordinal = 0)Entity entity){
        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, Items.ENDER_PEARL.getDefaultStack());
        }
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
