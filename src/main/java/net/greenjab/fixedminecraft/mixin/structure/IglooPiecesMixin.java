package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IglooPieces.class)
public abstract class IglooPiecesMixin {
    @Redirect(method = "addPieces", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextDouble()D"))
    private static double alwaysBasement(RandomSource instance) {
        return 0;
    }

}
