package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.other.FixedFurnaceMinecartEntity;
import net.minecraft.core.BlockPos;
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
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

    @ModifyExpressionValue(method = "comeOffTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;getMaxSpeed(Lnet/minecraft/server/level/ServerLevel;)D"))
    private double clampTo40(double original) {
        return 40;
    }
    @WrapOperation(method = "comeOffTrack", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;", ordinal = 1))
    private Vec3 noAirDrag(Vec3 instance, double scale, Operation<Vec3> original) {
        return original.call(instance, 1.0).multiply(1, this.isInWater()?0.95:1, 1);
    }
    public void onAboveBubbleColumn(final boolean dragDown, final @NonNull BlockPos pos){
        super.onAboveBubbleColumn(dragDown, pos);
        Vec3 v = this.getDeltaMovement();
        this.setDeltaMovement(v.x, Math.min(v.y, 0.7), v.z);
    }
    @ModifyArg(method = "comeOffTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
    private double groundFriction(double scale) {
        return this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction();
    }

    @WrapOperation(method = "pushOtherMinecart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;push(DDD)V"))
    private void furnaceMinecartsCantBePushed(AbstractMinecart instance, double x, double y, double z, Operation<Void> original){
        if (!(instance instanceof MinecartFurnace)) original.call(instance, x, y, z);
    }
    @WrapOperation(method = "pushOtherMinecart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void furnaceMinecartsCantBePushed2(AbstractMinecart instance, Vec3 vec3, Operation<Void> original){
        if (!(instance instanceof MinecartFurnace)) original.call(instance, vec3);
    }
    @WrapOperation(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;push(DDD)V"))
    private void trainMinecartsCantBePushed(AbstractMinecart instance, double x, double y, double z, Operation<Void> original){
        if (instance instanceof FixedFurnaceMinecartEntity furnaceMinecart && furnaceMinecart.isPowered()) return;
        if (instance.entityTags().contains("train")) return;
        original.call(instance, x, y, z);
    }

    @Inject(method = "canCollideWith", at = @At(value = "HEAD"), cancellable = true)
    private void playerNoStopTrain(Entity entity, CallbackInfoReturnable<Boolean> cir){
        if (entity instanceof AbstractMinecart) return;
        Entity thisEntity = this;
        if (thisEntity instanceof FixedFurnaceMinecartEntity) cir.setReturnValue(false);
        else if (thisEntity.entityTags().contains("train")) cir.setReturnValue(false);
    }
    @Inject(method = "canCollideWith", at = @At(value = "RETURN"), cancellable = true)
    private void removeTrainCollisions(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && entity instanceof AbstractMinecart) cir.setReturnValue(noInternalTrainCollisions(this, entity));
    }
    @Unique
    private boolean noInternalTrainCollisions(Entity thisEntity, Entity otherEntity) {
        if (thisEntity instanceof FixedFurnaceMinecartEntity fixedFurnaceMinecartEntity &&
            fixedFurnaceMinecartEntity.getTrain().contains(otherEntity)) return false;
        if (thisEntity.entityTags().contains("train") || thisEntity.entityTags().contains("trainTP")) {
            if (thisEntity.isOnRails()) return false;
            if (otherEntity.entityTags().contains("train") || otherEntity.entityTags().contains("trainTP")) return false;
            if (otherEntity instanceof FixedFurnaceMinecartEntity fixedFurnaceMinecartEntity)
                return !fixedFurnaceMinecartEntity.getTrain().contains(thisEntity);
        }
        return true;
    }

    @WrapOperation(method = "createMinecart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/NewMinecartBehavior;adjustToRails(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"))
    private static <T extends AbstractMinecart> void setSpawnRotation(
            NewMinecartBehavior controller, BlockPos targetBlockPos, BlockState currentState, boolean instant, Operation<Void> original, @Local T entity, @Local(argsOnly = true) Player player) {
        original.call(controller, targetBlockPos, currentState, instant);
        if (player != null && (entity instanceof MinecartFurnace)) {
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
        if (this.entityTags().contains("train")) this.addTag("trainMove");
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void removeLeash(CallbackInfo ci) {
        if (this.level().isClientSide() && this.tickCount>30) this.entityTags().clear();
    }
}
