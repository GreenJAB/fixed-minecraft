package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedFurnaceMinecartEntity;
import net.greenjab.fixedminecraft.registry.block.CopperRailBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperimentalMinecartController.class)
public abstract class ExperimentalMinecartControllerMixin extends MinecartController {

    protected ExperimentalMinecartControllerMixin(AbstractMinecartEntity minecart) {
        super(minecart);
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"),cancellable = true)
    private void copperSpeed(ServerWorld world, CallbackInfoReturnable<Double> cir) {
        BlockState state = world.getBlockState(this.minecart.getBlockPos());
        Vec3d velocity = this.minecart.getVelocity();
        double u =40;
        if (state.getBlock() instanceof AbstractRailBlock) {
            u = 8.0;
            if (state.getBlock() instanceof CopperRailBlock) {
                u = CopperRailBlock.getMaxVelocity(state);
            }
        }
        if (this.minecart.isTouchingWater()) u /= 2.0;
        u /= 20.0;
        u = Math.max(u, velocity.horizontalLength()*.9);
        if (state.getBlock() instanceof PoweredRailBlock && !(this.minecart instanceof FurnaceMinecartEntity)) {
            u = 8.0/20.0;
        }
        cir.setReturnValue(u);
        cir.cancel();
    }

    @Inject(method = "applySlopeVelocity", at = @At("HEAD"), cancellable = true)
    private void lessSlowDown(Vec3d horizontalVelocity, RailShape railShape, CallbackInfoReturnable<Vec3d> cir) {
        if (this.minecart instanceof FurnaceMinecartEntity) {
            cir.setReturnValue(horizontalVelocity);
            cir.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void noTrainPartUpdate(CallbackInfo ci) {
        for (Entity entity :this.minecart.getPassengerList()) {
            entity.fallDistance = 0;
            this.minecart.fallDistance = 0;
        }
        if (this.minecart.getCommandTags().contains("train")) {
            if (this.minecart.age>60) {
                this.minecart.removeCommandTag("train");
                this.minecart.removeCommandTag("trainMove");
            }
        }
        if (this.minecart.getCommandTags().contains("trainMove")) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/ExperimentalMinecartController;calcNewHorizontalVelocity(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/vehicle/ExperimentalMinecartController$MoveIteration;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/enums/RailShape;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d skipPowerRailSlowdown(Vec3d original) {
        if (this.minecart.noClip || this.minecart.getCommandTags().contains("train") || this.minecart.age<0) {
            return this.getVelocity().getHorizontal();
        }
        return original;
    }

    @Inject(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private void powerRailFurnaceMinecart(ServerWorld world, CallbackInfo ci, @Local BlockState blockState) {
        if (this.minecart instanceof FixedFurnaceMinecartEntity fixedFurnaceMinecartEntity) {
            if (blockState.isOf(Blocks.POWERED_RAIL)) {
                fixedFurnaceMinecartEntity.powerRailSetLit = blockState.get(PoweredRailBlock.POWERED)?1:-1;
            }
        }
    }
    @Inject(method = "getSpeedRetention", at = @At("HEAD"), cancellable = true)
    private void consistantSpeeds(CallbackInfoReturnable<Double> cir){
        if (this.minecart.age<0 || this.minecart.getCommandTags().contains("train")) {
            cir.setReturnValue(0.975);
            cir.cancel();
        }
    }
    @Redirect(method = "pushAwayFromEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;hasPassengers()Z"))
    private boolean test(AbstractMinecartEntity instance){
        return false;
    }
}
