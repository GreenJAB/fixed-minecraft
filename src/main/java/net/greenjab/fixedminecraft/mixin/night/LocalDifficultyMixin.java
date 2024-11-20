package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalDifficulty.class)
public class LocalDifficultyMixin {

    @ModifyVariable(method = "setLocalDifficulty", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private float inverseMoon(float x) {
        return 1 - x;
    }

    @Redirect(method = "setLocalDifficulty",at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F", ordinal = 2))
    private float moonMoreEffect(float moonSize, float min, float max) {
        return MathHelper.clamp(moonSize*3, min, max*3);
    }

}
