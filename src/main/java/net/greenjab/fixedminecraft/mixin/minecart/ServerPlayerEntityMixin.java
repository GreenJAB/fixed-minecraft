package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow
    public abstract void stopRiding();

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void dismountTrain(CallbackInfo ci){
        ServerPlayerEntity SPE = (ServerPlayerEntity)(Object)this;
        Entity vehicle = SPE.getVehicle();
        if (vehicle instanceof AbstractMinecartEntity minecart) {
            if (minecart.getCommandTags().contains("train"))  {
                this.stopRiding();
            }
        }
    }
}
