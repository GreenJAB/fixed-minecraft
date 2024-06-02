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
            float f = (float) (1.0f/(1.0f+(3*this.getVelocity().horizontalLength())));
            thi.pushX *= f;
            thi.pushZ *= f;
            Vec3d vec3d = this.getVelocity().add(thi.pushX/80.0f, 0.0, thi.pushZ/80.0f);
            if (this.isTouchingWater()) {
                vec3d = vec3d.multiply(0.1);
            }

            this.setVelocity(vec3d);
        } else {
            this.setVelocity(this.getVelocity().multiply(0.75, 0.0, 0.75));
        }
        ci.cancel();
    }
}
