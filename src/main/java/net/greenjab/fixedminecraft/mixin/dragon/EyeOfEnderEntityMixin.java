package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.entity.EyeOfEnderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EyeOfEnderEntity.class)
public class EyeOfEnderEntityMixin {

    @ModifyArg(method = "initTargetPos", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"
    ), index = 0)
    private int dontBreakEyes(int bound) {
        return 10;
    }
}
