package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderPearlMixin extends ThrowableItemProjectile {
    @Unique
    @Nullable
    private LivingEntity vehicle = null;

    public ThrownEnderPearlMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    /**
     * Saves the mounted entity to prevent silly teleportation exploit.
     * <br>
     * I.e. go to place A, sit in a boat and throw ender pearl into a bubble column.
     * Go to place B and mount a horse. Ask a friend or use redstone to trigger the ender pearl.
     * Voila, you have teleported your horse!
     */
    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private void saveVehicle(Level level, LivingEntity mob, ItemStack itemStack, CallbackInfo ci) {
        if (mob.isPassenger()) {
            LivingEntity root = rootVehicle(mob);
            if (root != null && mob == root.getControllingPassenger()) {
                vehicle = root;
            }
        }
    }
    /**
     * Teleports the player vehicle to the destination if it matches the saved one.
     *
     */
    @Redirect(
            method = "onHit", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;"
    ))
    private ServerPlayer teleportWithVehicle(ServerPlayer serverPlayerEntity, TeleportTransition transition,
                                                   @Share("passed")
                                     LocalBooleanRef ref) {
        CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, Items.ENDER_PEARL.getDefaultInstance());
        if (serverPlayerEntity.isPassenger() ) {
            LivingEntity currentVehicle = rootVehicle(serverPlayerEntity);
            if (currentVehicle != null && currentVehicle.equals(vehicle)) {
                vehicle.teleport(
                        new TeleportTransition((ServerLevel) this.level(), this.oldPosition(), Vec3.ZERO, 0.0F, 0.0F, Relative.union(Relative.ROTATION, Relative.DELTA), TeleportTransition.DO_NOTHING)
                );
                assert vehicle != null;
                vehicle.addTag("tp");

                if (vehicle instanceof PathfinderMob pathAwareEntity)
                    pathAwareEntity.getNavigation().stop();

                vehicle.resetFallDistance();
                ThrownEnderpearl EPE = (ThrownEnderpearl) (Object) this;
                if (!((Player) Objects.requireNonNull((EPE).getOwner())).hasInfiniteMaterials()) {
                    vehicle.hurtServer((ServerLevel) this.level(), this.damageSources().fall(), 5.0F);
                }
                ref.set(true);

                ServerPlayer serverPlayerEntity2 = serverPlayerEntity.teleport(
                        new TeleportTransition((ServerLevel)this.level(), this.oldPosition(), Vec3.ZERO, 0.0F, 0.0F, Relative.union(Relative.ROTATION, Relative.DELTA), TeleportTransition.DO_NOTHING)
                );
                assert serverPlayerEntity2 != null;
                serverPlayerEntity2.startRiding(vehicle);
                return serverPlayerEntity2;
            }
        }

        return serverPlayerEntity.teleport(
                new TeleportTransition((ServerLevel)this.level(), this.oldPosition(), Vec3.ZERO, 0.0F, 0.0F, Relative.union(Relative.ROTATION, Relative.DELTA), TeleportTransition.DO_NOTHING)
        );
    }

    /**
     * Recursively gets the bottommost vehicle.
     */
    @Unique
    @Nullable
    private LivingEntity rootVehicle(Entity entity) {
        if (!entity.isPassenger()) return null;
        if (!(entity.getVehicle() instanceof LivingEntity veh)) return null;
        LivingEntity subVehicle = rootVehicle(veh);
        return subVehicle == null ? veh : subVehicle;
    }
}
