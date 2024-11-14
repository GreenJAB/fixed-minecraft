package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.HugeMushroomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HugeMushroomFeature.class)
public abstract class HugeMushroomFeatureMixin {
    @Redirect(method = "canGenerate", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/feature/HugeMushroomFeature;isSoil(Lnet/minecraft/block/BlockState;)Z"
    ))
    private boolean genOnStone(BlockState blockState) {
        return true;
    }
}
