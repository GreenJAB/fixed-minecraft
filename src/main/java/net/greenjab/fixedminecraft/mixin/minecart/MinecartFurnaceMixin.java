package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecartFurnace.class)
public abstract class MinecartFurnaceMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"), cancellable = true)
    private void cancelTick(CallbackInfo ci) { ci.cancel(); }
    @Inject(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ValueOutput;putDouble(Ljava/lang/String;D)V"), cancellable = true)
    private void cancelWrite(CallbackInfo ci) { ci.cancel(); }
    @Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ValueInput;getDoubleOr(Ljava/lang/String;D)D"), cancellable = true)
    private void cancelRead(CallbackInfo ci) { ci.cancel(); }

    @ModifyConstant(method = "getMaxSpeed", constant = @Constant(doubleValue = 0.5))
    private double notReducedSpeed(double constant) { return 1;}
}
