package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.block.FlowerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FlowerBlock.class)
public class FlowerBlockMixin {

    @ModifyVariable(method = "createStewEffectList", at = @At("HEAD"), argsOnly = true)
    private static float longerSaturationSoup(float length) {
        return 10f;
    }
}
