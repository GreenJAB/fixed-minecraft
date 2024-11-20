package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.world.gen.feature.HugeMushroomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(HugeMushroomFeature.class)
public abstract class HugeMushroomFeatureMixin {
    @Redirect(method = "canGenerate", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/feature/HugeMushroomFeature;isSoil(Lnet/minecraft/block/BlockState;)Z"
    ))
    private boolean genOnStone(BlockState blockState) {
        return blockState.isIn(BlockTags.DIRT) ||
               blockState.isIn(BlockTags.BASE_STONE_OVERWORLD);
    }
}
