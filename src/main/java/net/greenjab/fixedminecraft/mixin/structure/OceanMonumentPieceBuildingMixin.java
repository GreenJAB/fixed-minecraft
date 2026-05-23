package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(OceanMonumentPieces.MonumentBuilding.class)
public abstract class OceanMonumentPieceBuildingMixin {

    @ModifyVariable(method = "postProcess", at = @At("STORE"), ordinal = 0)
    private int lowerMonument(int waterHeight) {
        return waterHeight - 25;
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/util/RandomSource;IILnet/minecraft/core/Direction;)V", constant = @Constant(intValue = 39))
    private static int lowerMonument2(int x) {
        return x-25;
    }

}
