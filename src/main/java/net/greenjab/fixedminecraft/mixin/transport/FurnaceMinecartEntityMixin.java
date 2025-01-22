package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartEntityMixin extends VehicleEntity {
    public FurnaceMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "applySlowdown", at = @At("HEAD"),cancellable = true)
    private void modifyMaxVelocity(Vec3d velocity, CallbackInfoReturnable<Vec3d> cir) {
        FurnaceMinecartEntity thi = (FurnaceMinecartEntity)(Object) this;
        double d = thi.pushVec.getX() * thi.pushVec.getX() + thi.pushVec.getZ() * thi.pushVec.getZ();
        if (d > 1.0E-7) {
            d = Math.sqrt(d);
            thi.pushVec.multiply(1/d);
            float f = (float) (1.0f/(1.0f+(3*this.getVelocity().horizontalLength())));
            thi.pushVec.multiply(f);
            Vec3d vec3d = this.getVelocity().add(thi.pushVec.getX()/80.0f, 0.0, thi.pushVec.getZ()/80.0f);
            if (this.isTouchingWater()) {
                vec3d = vec3d.multiply(0.1);
            }

            this.setVelocity(vec3d);
        } else {
            this.setVelocity(this.getVelocity().multiply(0.75, 0.0, 0.75));
        }
        cir.cancel();
    }
}
