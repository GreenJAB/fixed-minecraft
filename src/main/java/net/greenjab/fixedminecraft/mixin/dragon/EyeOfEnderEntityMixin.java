package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EyeOfEnderEntity.class)
public class EyeOfEnderEntityMixin {

    @Redirect(method = "initTargetPos", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"
    ))
    private int dontBreakEyes(Random instance, int i) {
        return 1;
    }
}
