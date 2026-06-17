package net.greenjab.fixedminecraft.mixin.structure;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.component.LodestoneTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LodestoneTracker.class)
public abstract class LodestoneTrackerMixin {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;existsAtPosition(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean trackUngeneratedChunks(boolean original, @Local(argsOnly = true) ServerLevel level,
                                           @Local BlockPos blockPos) {
        return original || !level.hasChunkAt(blockPos);
    }
}
