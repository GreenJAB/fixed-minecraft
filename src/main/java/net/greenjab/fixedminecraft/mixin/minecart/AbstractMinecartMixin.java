package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.other.DispencerMinecartEntity;
import net.greenjab.fixedminecraft.registry.other.FixedFurnaceMinecartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends VehicleEntity {
    @Shadow
    public abstract boolean isOnRails();

    public AbstractMinecartMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "useExperimentalMovement", at = @At(value = "HEAD"),cancellable = true)
    private static void improvedMinecarts(Level level, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
        cir.cancel();
    }

    @Inject(method = "comeOffTrack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 1
    ), cancellable = true)
    private void noAirDragInitially(ServerLevel level, CallbackInfo ci) {
        if (this.getDeltaMovement().y()>-0.7) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.95, 1));
            ci.cancel();
        }
    }
    @Redirect(method = "comeOffTrack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;getMaxSpeed(Lnet/minecraft/server/level/ServerLevel;)D"))
    private double clampTo40(AbstractMinecart instance, ServerLevel level) {
        return 40;
    }

    @Redirect(method = "comeOffTrack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private void groundFriction(AbstractMinecart instance, Vec3 vec3d) {
        instance.setDeltaMovement(instance.getDeltaMovement().scale(this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction()));
    }

    @Redirect(method = "pushOtherMinecart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;push(DDD)V"))
    private void furnaceMinecartsCantBePushed(AbstractMinecart instance, double x, double y, double z){
        if (!(instance instanceof MinecartFurnace)) {
            instance.push(x, y, z);
        }
    }
    @Redirect(method = "pushOtherMinecart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void furnaceMinecartsCantBePushed2(AbstractMinecart instance, Vec3 vec3d){
        if (!(instance instanceof MinecartFurnace)) {
            instance.setDeltaMovement(vec3d);
        }
    }
    @Redirect(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;push(DDD)V"))
    private void trainMinecartsCantBePushed(AbstractMinecart instance, double x, double y, double z){
        if (!(instance.entityTags().contains("train"))) {
            instance.push(x, y, z);
        }
    }
    @Redirect(method = "createMinecart", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/minecart/NewMinecartBehavior;adjustToRails(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"
    ))
    private static <T extends AbstractMinecart> void setSpawnRotation(NewMinecartBehavior controller, BlockPos targetBlockPos,
                                                                      BlockState currentState, boolean instant,
                                                                      @Local T entity,
                                                                      @Local(argsOnly = true) Player player) {
        controller.adjustToRails(targetBlockPos, currentState, true);
        if (player != null && (entity instanceof MinecartFurnace || entity instanceof DispencerMinecartEntity)) {
            float rot = (-player.yHeadRot -90+720)%360;
            if (Math.cos((rot - entity.getYRot()) * Math.PI / 180f) < 0) {
                entity.setYRot((entity.getYRot() + 180) % 360);
                entity.setDeltaMovement(0, 0.001f, 0);
                entity.setFlipped(true);
            }
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void removeTrainTag(ValueInput input, CallbackInfo ci) {
        this.tickCount=0;
        this.removeTag("trainNoEngine");
        if (this.entityTags().contains("train")) {
            this.addTag("trainMove");
        }
    }

    @Inject(method = "canCollideWith", at = @At(value = "RETURN"), cancellable = true)
    private void removeTrainCollisions(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (entity instanceof AbstractMinecart) {
                cir.setReturnValue(noInternalTrainCollisions(this, entity));
            }
        }
    }

    @Unique
    private boolean noInternalTrainCollisions(Entity thisEntity, Entity otherEntity) {
        if (thisEntity instanceof FixedFurnaceMinecartEntity fixedFurnaceMinecartEntity) {
            if (fixedFurnaceMinecartEntity.getTrain().contains(otherEntity)) {
                return false;
            }
        }
        if (thisEntity.entityTags().contains("train")||thisEntity.entityTags().contains("trainTP")) {
            if (thisEntity.isOnRails()) {
                return false;
            }
            if (otherEntity.entityTags().contains("train")||otherEntity.entityTags().contains("trainTP")) {
                return false;
            }
            if (otherEntity instanceof FixedFurnaceMinecartEntity fixedFurnaceMinecartEntity) {
                return !fixedFurnaceMinecartEntity.getTrain().contains(thisEntity);
            }
        }
        return true;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void removeLeash(CallbackInfo ci) {
        if (this.level().isClientSide()) {
            if (this.tickCount==30) {
                this.entityTags().clear();
            }
        }
    }
}
