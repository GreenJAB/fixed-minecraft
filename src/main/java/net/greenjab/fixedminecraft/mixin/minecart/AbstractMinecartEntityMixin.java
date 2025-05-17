package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.block.CopperRailBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
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

    @Redirect(method = "moveOffRail", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;getMaxSpeed()D"))
    private double clampTo40(AbstractMinecartEntity instance) {
        return 20;
    }

    @Redirect(method = "moveOffRail", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    private void groundFriction(AbstractMinecartEntity instance, Vec3d vec3d) {
        instance.setVelocity(instance.getVelocity().multiply(this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness()));
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"),cancellable = true)
    private void copperSpeed(CallbackInfoReturnable<Double> cir) {
        AbstractMinecartEntity minecart = (AbstractMinecartEntity)(Object)this;
        BlockState state = minecart.getWorld().getBlockState(this.getBlockPos());
        Vec3d velocity = this.getVelocity();
        double u =40;
        if (state.getBlock() instanceof AbstractRailBlock) {
            u = 8.0;
            if (state.getBlock() instanceof CopperRailBlock) {
                u = CopperRailBlock.getMaxVelocity(state);
            }
        }
        if (state.getBlock() instanceof PoweredRailBlock && !(minecart instanceof FurnaceMinecartEntity)) {
            u = 8.0/20.0;
        }
        if (this.isTouchingWater()) u /= 2.0;
        u /= 20.0;
        u = Math.max(u, velocity.horizontalLength()*.9);
        cir.setReturnValue(u);
        cir.cancel();
    }

}
