package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.block.CopperRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartEntityMixin extends VehicleEntity {
    public FurnaceMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "applySlowdown", at = @At("HEAD"),cancellable = true)
    private void modifyMaxVelocity(CallbackInfo ci) {
        FurnaceMinecartEntity thi = (FurnaceMinecartEntity)(Object) this;
        double d = thi.pushX * thi.pushX + thi.pushZ * thi.pushZ;
        if (d > 1.0E-7) {
            d = Math.sqrt(d);
            thi.pushX /= d;
            thi.pushZ /= d;
            float f = (float) (1.0f/(1.0f+(2*this.getVelocity().horizontalLength())));
            thi.pushX *= f;
            thi.pushZ *= f;
            Vec3d vec3d = this.getVelocity().add(thi.pushX/50.0f, 0.0, thi.pushZ/50.0f);
            if (this.isTouchingWater()) {
                vec3d = vec3d.multiply(0.1);
            }

            this.setVelocity(vec3d);
        } else {
            this.setVelocity(this.getVelocity().multiply(0.75, 0.0, 0.75));
        }
        ci.cancel();
    }

    /*@Inject(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;horizontalLengthSquared()D"),cancellable = true)
    private void dontReverse(CallbackInfo ci) {
        FurnaceMinecartEntity thi = (FurnaceMinecartEntity)(Object) this;
        Vec3d vec3d = this.getVelocity();
        double f = vec3d.horizontalLengthSquared();
        double g = thi.pushX * thi.pushX + thi.pushZ * thi.pushZ;
        if (g > 1.0E-4 && f > 0.001) {
            double h = Math.sqrt(f);
            double i = Math.sqrt(g);
            System.out.println(thi.pushX +", "+thi.pushZ);
            if (vec3d.dotProduct(new Vec3d(thi.pushX, 0, thi.pushZ)) >-0.5) {
                thi.pushX = vec3d.x / h * i;
                thi.pushZ = vec3d.z / h * i;
            }


        }
        ci.cancel();
    }*/
}
