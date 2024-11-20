package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.structure.OceanMonumentGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(OceanMonumentGenerator.Base.class)
public abstract class OceanMonumentGeneratorBaseMixin {

    @ModifyVariable(method = "generate", at = @At("STORE"), ordinal = 1)
    private int lowerMonument(int x) {
        return x-25;
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/util/math/random/Random;IILnet/minecraft/util/math/Direction;)V", constant = @Constant(intValue = 39))
    private static int lowerMonument2(int x) {
        return x-25;
    }

}
