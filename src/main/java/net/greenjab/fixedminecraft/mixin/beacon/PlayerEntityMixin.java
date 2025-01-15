package net.greenjab.fixedminecraft.mixin.beacon;

import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {


    @Inject(method = "getBlockInteractionRange", at = @At(value = "RETURN"))
    private void longerBlockReach(CallbackInfoReturnable<Double> cir) {
        double d =  cir.getReturnValueD();
        PlayerEntity PE = (PlayerEntity)(Object)this;
        if (PE.hasStatusEffect(StatusRegistry.INSTANCE.getREACH())) {
            d+=0.5*(1+PE.getStatusEffect(StatusRegistry.INSTANCE.getREACH()).getAmplifier());
        }
        cir.setReturnValue(d);
    }
}
