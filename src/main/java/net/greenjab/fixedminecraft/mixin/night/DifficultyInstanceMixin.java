package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DifficultyInstance.class)
public abstract class DifficultyInstanceMixin {

    @ModifyVariable(method = "calculateDifficulty", at = @At(value = "HEAD"), argsOnly = true, ordinal = 0)
    private float inverseMoon(float moonBrightness) {
        return 1 - moonBrightness;
    }

    @WrapOperation(method = "calculateDifficulty", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 2))
    private float moonMoreEffect(float value, float min, float max, Operation<Float> original) {
        return original.call(value * 3, min, max * 3);
    }
}
