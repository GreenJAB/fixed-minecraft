package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.world.level.block.FlowerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FlowerBlock.class)
public abstract class FlowerBlockMixin {

    @ModifyVariable(method = "makeEffectList", at = @At("HEAD"), argsOnly = true)
    private static float longerSaturationSoup(float effectSeconds) {
        return 10f;
    }
}
