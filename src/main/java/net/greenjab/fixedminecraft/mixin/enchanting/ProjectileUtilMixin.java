package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {

    @Redirect(method = "getEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;F)Lnet/minecraft/util/hit/EntityHitResult;",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;raycast(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Ljava/util/Optional;"))
    private static Optional<Vec3d> collideIfAlreadyInHitbox(Box instance, Vec3d from, Vec3d to,
                                         @Local(argsOnly = true)Entity arrow, @Local(argsOnly = true) float margin) {
        if (arrow instanceof ProjectileEntity) {
            if (instance.contains(from) && arrow.age>2) {
                return Optional.of(from);
            }
        }
        return instance.raycast(from, to);
    }
}
