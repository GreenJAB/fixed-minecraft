package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.other.FixedFurnaceMinecartEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import net.greenjab.fixedminecraft.registry.block.CopperRailBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {

    protected NewMinecartBehaviorMixin(AbstractMinecart minecart) {
        super(minecart);
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"),cancellable = true)
    private void copperSpeed(ServerLevel level, CallbackInfoReturnable<Double> cir) {
        BlockState state = level.getBlockState(this.minecart.blockPosition());
        Vec3 velocity = this.minecart.getDeltaMovement();
        double u =40;
        if (state.getBlock() instanceof BaseRailBlock) {
            u = 8.0;
            if (state.getBlock() instanceof CopperRailBlock) {
                u = CopperRailBlock.getMaxVelocity(state);
            }
        }
        if (this.minecart.isInWater()) u /= 2.0;
        u /= 20.0;
        u = Math.max(u, velocity.horizontalDistance()*.9);
        if (state.getBlock() instanceof PoweredRailBlock && !(this.minecart instanceof MinecartFurnace)) {
            u = 8.0/20.0;
        }
        cir.setReturnValue(u);
        cir.cancel();
    }

    @Inject(method = "calculateSlopeSpeed", at = @At("HEAD"), cancellable = true)
    private void lessSlowDown(Vec3 deltaMovement, RailShape shape, CallbackInfoReturnable<Vec3> cir) {
        if (this.minecart instanceof MinecartFurnace) {
            cir.setReturnValue(deltaMovement);
            cir.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void noTrainPartUpdate(CallbackInfo ci) {
        for (Entity entity :this.minecart.getPassengers()) {
            entity.fallDistance = 0;
            this.minecart.fallDistance = 0;
        }
        if (this.minecart.tickCount>60) {
            this.minecart.removeTag("train");
            this.minecart.removeTag("trainMove");
            this.minecart.removeTag("trainTP");
        }
        if (this.minecart.entityTags().contains("trainMove")) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/NewMinecartBehavior;calculateTrackSpeed(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/vehicle/minecart/NewMinecartBehavior$TrackIteration;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/properties/RailShape;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 skipPowerRailSlowdown(Vec3 original) {
        if (this.minecart.noPhysics || this.minecart.entityTags().contains("train")) {
            return this.getDeltaMovement().horizontal();
        }
        return original;
    }

    @Inject(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private void powerRailFurnaceMinecart(ServerLevel level, CallbackInfo ci, @Local BlockState currentState) {
        if (this.minecart instanceof FixedFurnaceMinecartEntity fixedFurnaceMinecartEntity) {
            if (currentState.is(Blocks.POWERED_RAIL)) {
                fixedFurnaceMinecartEntity.powerRailSetLit = currentState.getValue(PoweredRailBlock.POWERED)?1:-1;
            }
        }
    }
    @Inject(method = "getSlowdownFactor", at = @At("HEAD"), cancellable = true)
    private void consistantSpeeds(CallbackInfoReturnable<Double> cir){
        if (this.minecart.tickCount<0 || this.minecart.entityTags().contains("train")) {
            cir.setReturnValue(0.975);
            cir.cancel();
        }
    }
    @Redirect(method = "pushEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;isVehicle()Z"))
    private boolean test(AbstractMinecart instance){
        return false;
    }
}
