package net.greenjab.fixedminecraft.mixin.effects;

import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "blockInteractionRange", at = @At(value = "RETURN"), cancellable = true)
    private void longerBlockReach(CallbackInfoReturnable<Double> cir) {
        double d =  cir.getReturnValueD();
        Player PE = (Player)(Object)this;
        if (PE.hasEffect(MobEffectRegistry.REACH)) {
            d+=0.5*(1+PE.getEffect(MobEffectRegistry.REACH).getAmplifier());
        }
        cir.setReturnValue(d);
    }
}
