package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.entity.projectile.ArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowEntity.class)
public class ArrowEntityMixin {

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void removeParticlesIfPiecing(CallbackInfoReturnable<Integer> cir) {
        ArrowEntity AE = (ArrowEntity)(Object)this;
        if (AE.getCommandTags().contains("pierced")) {
            cir.setReturnValue(-1);
            cir.cancel();
        }
    }
}
