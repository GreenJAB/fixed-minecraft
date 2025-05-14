package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceMinecartEntity.class)
public class FurnaceMinecartEntityMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isClient()Z"), cancellable = true)
    private void cancelTick(CallbackInfo ci) { ci.cancel(); }
    @Inject(method = "writeCustomData", at = @At(value = "INVOKE", target = "Lnet/minecraft/storage/WriteView;putDouble(Ljava/lang/String;D)V"), cancellable = true)
    private void cancelWrite(CallbackInfo ci) { ci.cancel(); }
    @Inject(method = "readCustomData", at = @At(value = "INVOKE", target = "Lnet/minecraft/storage/ReadView;getDouble(Ljava/lang/String;D)D"), cancellable = true)
    private void cancelRead(CallbackInfo ci) { ci.cancel(); }

    @ModifyConstant(method = "getMaxSpeed", constant = @Constant(doubleValue = 0.5))
    private double notReducedSpeed(double constant) { return 1;}
}
