package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.structure.IglooGenerator;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;


@SuppressWarnings("unchecked")
@Mixin(IglooGenerator.class)
public abstract class IglooGeneratorMixin {
    @Redirect(method = "addPieces", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextDouble()D"))
    private static double alwaysBasement(Random instance) {
        return 0;
    }

}
