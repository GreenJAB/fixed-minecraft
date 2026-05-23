package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.world.entity.projectile.EyeOfEnder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EyeOfEnder.class)
public abstract class EyeOfEnderMixin {

    @ModifyArg(method = "signalTo", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"
    ), index = 0)
    private int dontBreakEyes(int bound) {
        return 10;
    }
}
