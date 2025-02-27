package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartEntityMixin extends AbstractMinecartEntity {
    public FurnaceMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }
    @ModifyArg(method = "applySlowdown", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;applySlowdown(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"
    ))
    private Vec3d modifyMaxVelocity(Vec3d velocity) {
        FurnaceMinecartEntity thi = (FurnaceMinecartEntity)(Object) this;
        double d = thi.pushVec.getX() * thi.pushVec.getX() + thi.pushVec.getZ() * thi.pushVec.getZ();
        Vec3d vec3d;
        if (d > 1.0E-7) {
            d = Math.sqrt(d);
            thi.pushVec.multiply(1/d);
            float f = (float) (1.0f/(1.0f+(3*this.getVelocity().horizontalLength())));
            thi.pushVec.multiply(f);
            vec3d = this.getVelocity().add(thi.pushVec.getX()/80.0f, 0.0, thi.pushVec.getZ()/80.0f);
            if (this.isTouchingWater()) {
                vec3d = vec3d.multiply(0.1);
            }
        } else {
            vec3d = velocity.multiply(0.75, 0.0, 0.75);
        }
        return vec3d;
    }
}
