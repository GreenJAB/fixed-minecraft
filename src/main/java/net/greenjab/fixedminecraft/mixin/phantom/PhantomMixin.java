package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Phantom.class)
public abstract class PhantomMixin {

    @Shadow
    private Vec3 moveTargetPoint;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void diveTerrariaBossStyle2(CallbackInfo ci){
        Phantom PE = (Phantom)(Object)this;
        if (!PE.level().isClientSide()) {
            if (PE.level().isBrightOutside()) {
                PE.noPhysics = true;
            }
            if (PE.noPhysics) {
                this.moveTargetPoint = PE.position().add(0, -30, 0);
                PE.setDeltaMovement((PE.getDeltaMovement().scale(0.98)).add(0, -0.1, 0));
                float n = (float) (-(Mth.atan2(-PE.getDeltaMovement().y, PE.getDeltaMovement().horizontalDistance()) * 180.0F /
                                     (float) Math.PI));
                PE.setXRot(n);
            }
        }
    }

}
