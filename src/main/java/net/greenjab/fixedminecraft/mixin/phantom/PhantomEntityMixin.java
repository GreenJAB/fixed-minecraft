package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomEntity.class)
public abstract class PhantomEntityMixin {

    @Shadow
    Vec3d targetPosition;

    /*@Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/PhantomEntity;setOnFireFor(F)V", shift = At.Shift.AFTER))
    private void diveTerrariaBossStyle(CallbackInfo ci){
        PhantomEntity PE = (PhantomEntity)(Object)this;
        PE.setFireTicks(0);
    }*/
    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void diveTerrariaBossStyle2(CallbackInfo ci){
        PhantomEntity PE = (PhantomEntity)(Object)this;
        if (!PE.getEntityWorld().isClient()) {
            if (PE.getEntityWorld().isDay()) {
                PE.noClip = true;
            }
            if (PE.noClip) {
                this.targetPosition = PE.getEntityPos().add(0, -30, 0);
                PE.setVelocity((PE.getVelocity().multiply(0.98)).add(0, -0.1, 0));
                float n = (float) (-(MathHelper.atan2(-PE.getVelocity().y, PE.getVelocity().horizontalLength()) * 180.0F /
                                     (float) Math.PI));
                PE.setPitch(n);
            }
        }
    }

}
