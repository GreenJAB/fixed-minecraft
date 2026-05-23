package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {

    @Inject(method = "computeMargin", at = @At(value = "HEAD"), cancellable = true)
    private static void hitClose(Entity source, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0.3f);
    }

}
