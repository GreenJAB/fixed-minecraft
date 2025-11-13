package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LodestoneTrackerComponent.class)
public class LodestoneTrackerComponentMixin {

    @ModifyExpressionValue(
            method = "forWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/poi/PointOfInterestStorage;hasTypeAt(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/util/math/BlockPos;)Z"
            )
    )
    private boolean trackUngeneratedChunks(boolean original, @Local(argsOnly = true) ServerWorld world, @Local BlockPos blockPos) {
        return original || !world.isChunkLoaded(blockPos);
    }
}
