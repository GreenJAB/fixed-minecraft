package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {

    @Inject(method = "getToleranceMargin", at = @At(value = "HEAD"), cancellable = true)
    private static void test4(Entity entity, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0.3f);
    }

}
