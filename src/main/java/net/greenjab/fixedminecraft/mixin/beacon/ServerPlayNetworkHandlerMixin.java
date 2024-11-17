package net.greenjab.fixedminecraft.mixin.beacon;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @ModifyExpressionValue(method = "onPlayerInteractBlock", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"
    ))
    private double largerReach(double original) {
        double d =  Math.sqrt(original);
        if (this.player.hasStatusEffect(StatusRegistry.INSTANCE.getREACH())) {
            d+=0.5*(1+this.player.getStatusEffect(StatusRegistry.INSTANCE.getREACH()).getAmplifier());
        }
        return d*d;
    }

    @ModifyConstant(method = "onPlayerInteractBlock", constant = @Constant(doubleValue = 64.0))
    private double largerReach2(double original) {
        double d =  Math.sqrt(original);
        if (this.player.hasStatusEffect(StatusRegistry.INSTANCE.getREACH())) {
            d+=0.5*(1+this.player.getStatusEffect(StatusRegistry.INSTANCE.getREACH()).getAmplifier());
        }
        return d*d;
    }
}
