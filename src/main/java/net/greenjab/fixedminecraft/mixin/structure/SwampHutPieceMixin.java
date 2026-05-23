package net.greenjab.fixedminecraft.mixin.structure;

import net.greenjab.fixedminecraft.registry.registries.LootTableRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutPiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwampHutPiece.class)
public abstract class SwampHutPieceMixin extends StructurePiece {
    protected SwampHutPieceMixin(StructurePieceType type, int genDepth, BoundingBox boundingBox) {
        super(type, genDepth, boundingBox);
    }

    @Inject(method = "postProcess", at = @At(value = "TAIL"))
    private void EnchantingTableSetpiece(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                                         RandomSource random, BoundingBox chunkBB, ChunkPos chunkPos, BlockPos referencePos, CallbackInfo ci) {

        this.createChest(level, chunkBB, random, 2, 2, 6, LootTableRegistry.SWAMP_HUT);

    }

}
