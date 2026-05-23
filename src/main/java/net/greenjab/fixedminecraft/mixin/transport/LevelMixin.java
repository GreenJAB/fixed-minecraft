package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    private void delayRainEffect(CallbackInfoReturnable<Boolean> cir) {
        Level world =  (Level)(Object) this;
        cir.setReturnValue(world.getRainLevel(1.0f)>0.99f);
    }
}
