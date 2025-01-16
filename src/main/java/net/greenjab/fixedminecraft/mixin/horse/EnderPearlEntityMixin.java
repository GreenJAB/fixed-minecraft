package net.greenjab.fixedminecraft.mixin.horse;

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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void saveVehicle(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci) {
        if (owner.hasVehicle()) {
            if (owner == rootVehicle(owner).getControllingPassenger()) {
                vehicle = rootVehicle(owner);
            }
        }
    }

    @Redirect(  method = "onCollision", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;detach()V"))
    private void teleportWithVehicle(Entity instance,
                                     @Share("passed")
                                     LocalBooleanRef ref) {
        LivingEntity currentVehicle = rootVehicle(instance);
        if (currentVehicle == null || !currentVehicle.equals(vehicle)) {
            instance.detach();
            ref.set(false);
        }
    }
    /**
     * Teleports the player vehicle to the destination if it matches the saved one.
     */
    @Inject(
            method = "onCollision", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;teleportTo(Lnet/minecraft/world/TeleportTarget;)Lnet/minecraft/server/network/ServerPlayerEntity;"
    ))
    private void teleportWithVehicle(HitResult hitResult, CallbackInfo ci, @Local ServerPlayerEntity player,
                                     @Share("passed")
                                     LocalBooleanRef ref) {
        Criteria.CONSUME_ITEM.trigger(player, Items.ENDER_PEARL.getDefaultStack());
        if (player.hasVehicle()) {
            vehicle.teleportTo(
                    new TeleportTarget((ServerWorld) this.getWorld(), this.getLastRenderPos(), Vec3d.ZERO, 0.0F, 0.0F, PositionFlag.combine(PositionFlag.ROT, PositionFlag.DELTA), TeleportTarget.NO_OP)
            );
               // vehicle.requestTeleport(x, y, z);
                vehicle.addCommandTag("tp");

                if (vehicle instanceof PathAwareEntity pathAwareEntity)
                    pathAwareEntity.getNavigation().stop();

                vehicle.onLanding();
                EnderPearlEntity EPE = (EnderPearlEntity) (Object) this;
                if (!((PlayerEntity) Objects.requireNonNull((EPE).getOwner())).getAbilities().creativeMode) {
                    vehicle.damage((ServerWorld) this.getWorld(), this.getDamageSources().fall(), 5.0F);
                }
                ref.set(true);
        }
    }

    /**
     * Recursively gets the bottommost vehicle.
     */
    @Unique
    @Nullable
    private LivingEntity rootVehicle(Entity entity) {
        if (!entity.hasVehicle()) return null;
        if (!(entity.getVehicle() instanceof LivingEntity veh)) return null;
        LivingEntity subVehicle = rootVehicle(veh);
        return subVehicle == null ? veh : subVehicle;
    }
}
