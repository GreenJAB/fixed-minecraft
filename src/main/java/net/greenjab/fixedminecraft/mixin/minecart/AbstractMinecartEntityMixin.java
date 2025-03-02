package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends VehicleEntity {
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
    ), cancellable = true
    )
    private void noAirDragInitially(ServerWorld world, CallbackInfo ci) {
        System.out.println(this.getVelocity().horizontalLength());
        if (this.getVelocity().getY()>-0.7) {
            this.setVelocity(this.getVelocity().multiply(1, 0.95, 1));
            ci.cancel();
        }
    }
}
