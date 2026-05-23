package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void dismountTrain(CallbackInfo ci){
        ServerPlayer SPE = (ServerPlayer)(Object)this;
        Entity vehicle = SPE.getVehicle();
        if (vehicle instanceof AbstractMinecart minecart) {
            if (minecart.entityTags().contains("train"))  {
                SPE.stopRiding();
            }
        }
    }
}
