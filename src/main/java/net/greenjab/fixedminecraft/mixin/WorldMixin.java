package net.greenjab.fixedminecraft.mixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {

    /*@Inject(method = "calculateAmbientDarkness", at = @At("HEAD"))
    private void injected(CallbackInfo ci) {
        World w =  (World)(Object) this;
        System.out.println(w.getRainGradient(1.0f)+"");
    }*/
    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable cir) {
        World world =  (World)(Object) this;
        //System.out.println(world.getRainGradient(1.0f)+"");
        cir.setReturnValue(world.getRainGradient(1.0f)>0.99f);
    }
}
