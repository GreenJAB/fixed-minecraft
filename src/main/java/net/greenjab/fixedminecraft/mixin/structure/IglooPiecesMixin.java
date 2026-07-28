package net.greenjab.fixedminecraft.mixin.structure;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IglooPieces.class)
public abstract class IglooPiecesMixin {
    @WrapOperation(method = "addPieces", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextDouble()D"))
    private static double alwaysBasement(RandomSource instance, Operation<Double> original) {
        if (FixedMinecraft.isChangesEnabled("structures")) return 0;
        return original.call(instance);
    }
}
