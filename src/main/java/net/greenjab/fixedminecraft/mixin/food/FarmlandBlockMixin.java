package net.greenjab.fixedminecraft.mixin.food;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.FarmlandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {

    @ModifyConstant(method = "fallOn", constant = @Constant(doubleValue = 0.5))
    private static double requireGreaterFall(double constant, @Local(argsOnly = true) double fallDistance) {
        System.out.println(fallDistance);
        return 1.4;
    }
}
