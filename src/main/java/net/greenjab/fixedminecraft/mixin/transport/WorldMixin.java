package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        World world =  (World)(Object) this;
        cir.setReturnValue(world.getRainGradient(1.0f)>0.99f);
    }
}
