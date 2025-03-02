package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.block.CopperRailBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperimentalMinecartController.class)
public abstract class ExperimentalMinecartControllerMixin extends MinecartController {


    protected ExperimentalMinecartControllerMixin(AbstractMinecartEntity minecart) {
        super(minecart);
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"),cancellable = true)
    private void copperSpeed(ServerWorld world, CallbackInfoReturnable<Double> cir) {
        BlockState state = world.getBlockState(this.minecart.getBlockPos());
        Vec3d velocity = this.getVelocity();
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
}
