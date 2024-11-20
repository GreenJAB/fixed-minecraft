package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.block.*;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(StrongholdGenerator.Library.class)
public abstract class StrongholdGeneratorMixin extends StructurePiece {

    protected StrongholdGeneratorMixin(StructurePieceType type, int length, BlockBox boundingBox) {
        super(type, length, boundingBox);
    }

    @Inject(method = "generate", at = @At(value = "TAIL"))
    private void EnchantingTableSetpiece(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator,
                                Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot, CallbackInfo ci) {

        this.fill(world,chunkBox, 9,1, 5, 11, 3,9);
        this.addBlock(world, Blocks.OBSIDIAN.getDefaultState(), 10, 0, 7, chunkBox);
        this.fillWithOutline(world, chunkBox, 9, 1, 9, 11, 2, 9, Blocks.BOOKSHELF.getDefaultState(), Blocks.CHISELED_BOOKSHELF.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 9, 1, 5, 11, 2, 5, Blocks.BOOKSHELF.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 9, 2, 5, chunkBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 9, 2, 9, chunkBox);

        this.addBlock(world, Blocks.CHISELED_BOOKSHELF.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.SOUTH), 10, 1, 9, chunkBox);
        this.addBlock(world, Blocks.CHISELED_BOOKSHELF.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH), 9, 1, 5, chunkBox);
        this.addBlock(world, Blocks.CHISELED_BOOKSHELF.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.WEST), 12, 2, 8, chunkBox);
        this.addBlock(world, Blocks.CHISELED_BOOKSHELF.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.WEST), 12, 1, 6, chunkBox);

    }
}
