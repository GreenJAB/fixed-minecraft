package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DifficultyInstance.class)
public abstract class DifficultyInstanceMixin {

    @ModifyVariable(method = "calculateDifficulty", at = @At(value = "HEAD"), argsOnly = true, ordinal = 0)
    private float inverseMoon(float moonBrightness) {
        return 1 - moonBrightness;
    }

    @Redirect(method = "calculateDifficulty",at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 2))
    private float moonMoreEffect(float value, float min, float max) {
        return Mth.clamp(value * 3, min, max * 3);
    }

}
