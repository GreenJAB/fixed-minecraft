package net.greenjab.fixedminecraft.mixin.beacon;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @ModifyExpressionValue(method = "processBlockBreakingAction", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"
    ))
    private double longerBlockReach(double original) {
        double d =  Math.sqrt(original);
        if (this.player.hasStatusEffect(StatusRegistry.INSTANCE.getREACH())) {
            d+=0.5*(1+this.player.getStatusEffect(StatusRegistry.INSTANCE.getREACH()).getAmplifier());
        }
        return d*d;
    }
}
