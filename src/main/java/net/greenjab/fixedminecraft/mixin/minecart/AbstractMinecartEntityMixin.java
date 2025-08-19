package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.other.FixedFurnaceMinecartEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends VehicleEntity {
    @Shadow
    public abstract boolean isOnRail();

    public AbstractMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "areMinecartImprovementsEnabled", at = @At(value = "HEAD"),cancellable = true)
    private static void improvedMinecarts(World world, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
        cir.cancel();
    }

    @Inject(method = "moveOffRail", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 1
    ), cancellable = true)
    private void noAirDragInitially(ServerWorld world, CallbackInfo ci) {
        if (this.getVelocity().getY()>-0.7) {
            this.setVelocity(this.getVelocity().multiply(1, 0.95, 1));
            ci.cancel();
        }
    }
    @Redirect(method = "moveOffRail", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;getMaxSpeed(Lnet/minecraft/server/world/ServerWorld;)D"))
    private double clampTo40(AbstractMinecartEntity instance, ServerWorld world) {
        return 40;
    }

    @Redirect(method = "moveOffRail", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    private void groundFriction(AbstractMinecartEntity instance, Vec3d vec3d) {
        instance.setVelocity(instance.getVelocity().multiply(this.getEntityWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness()));
    }

    @Redirect(method = "pushAwayFromMinecart", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;addVelocity(DDD)V"))
    private void furnaceMinecartsCantBePushed(AbstractMinecartEntity instance, double x, double y, double z){
        if (!(instance instanceof FurnaceMinecartEntity)) {
            instance.addVelocity(x, y, z);
        }
    }
    @Redirect(method = "pushAwayFromMinecart", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    private void furnaceMinecartsCantBePushed2(AbstractMinecartEntity instance, Vec3d vec3d){
        if (!(instance instanceof FurnaceMinecartEntity)) {
            instance.setVelocity(vec3d);
        }
    }
    @Redirect(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;addVelocity(DDD)V"))
    private void trainMinecartsCantBePushed(AbstractMinecartEntity instance, double x, double y, double z){
        if (!(instance.getCommandTags().contains("train"))) {
            instance.addVelocity(x, y, z);
        }
    }
    @Redirect(method = "create", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/vehicle/ExperimentalMinecartController;adjustToRail(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V"
    ))
    private static <T extends AbstractMinecartEntity> void setSpawnRotation(ExperimentalMinecartController controller, BlockPos blockPos,
                                                                            BlockState blockState, boolean ignoreWeight,
                                                                            @Local T abstractMinecartEntity, @Local(argsOnly = true) PlayerEntity player) {
        controller.adjustToRail(blockPos, blockState, true);
        if (player != null && abstractMinecartEntity instanceof FurnaceMinecartEntity) {
            float rot = (-player.headYaw -90+720)%360;
            if (Math.cos((rot-abstractMinecartEntity.getYaw())*Math.PI/180f)<0) {
                abstractMinecartEntity.setYaw((abstractMinecartEntity.getYaw()+180)%360);
                abstractMinecartEntity.setVelocity(0, 0.001f, 0);
                abstractMinecartEntity.setYawFlipped(true);
            }
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void removeTrainTag(ReadView view, CallbackInfo ci) {
        this.age=0;
        this.removeCommandTag("trainNoEngine");
        if (this.getCommandTags().contains("train")) {
            this.addCommandTag("trainMove");
        }
    }

    @Inject(method = "collidesWith", at = @At(value = "RETURN"), cancellable = true)
    private void removeTrainCollisions(Entity otherEntity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (otherEntity instanceof AbstractMinecartEntity) {
                cir.setReturnValue(noInternalTrainCollisions(this, otherEntity));
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
        if (thisEntity.getCommandTags().contains("train")||thisEntity.getCommandTags().contains("trainTP")) {
            if (thisEntity.isOnRail()) {
                return false;
            }
            if (otherEntity.getCommandTags().contains("train")||otherEntity.getCommandTags().contains("trainTP")) {
                return false;
            }
            if (otherEntity instanceof FixedFurnaceMinecartEntity fixedFurnaceMinecartEntity) {
                if (fixedFurnaceMinecartEntity.getTrain().contains(thisEntity)) {
                    return false;
                }
            }
        }
        return true;
    }
}
