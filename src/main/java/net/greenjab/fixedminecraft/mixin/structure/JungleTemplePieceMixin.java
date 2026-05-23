package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.JungleTemplePiece;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JungleTemplePiece.class)
public abstract class JungleTemplePieceMixin extends ScatteredFeaturePiece {
    protected JungleTemplePieceMixin(StructurePieceType type, int x, int y, int z, int width, int height, int depth,
                                     Direction orientation) {
        super(type, x, y, z, width, height, depth, orientation);
    }

    @Inject(method = "postProcess", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/block/Blocks;CHISELED_STONE_BRICKS:Lnet/minecraft/world/level/block/Block;",
            opcode = Opcodes.GETSTATIC
    ), cancellable = true)
    private void betterRedstone(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                                RandomSource random, BoundingBox chunkBB, ChunkPos chunkPos, BlockPos referencePos, CallbackInfo ci) {
        this.placeBlock(level, Blocks.REDSTONE_WALL_TORCH.defaultBlockState().setValue(RedstoneTorchBlock.LIT, true).setValue(WallTorchBlock.FACING, Direction.EAST), 8, -1, 10, chunkBB);
        this.placeBlock(level, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 8, -3, 11, chunkBB);
        this.placeBlock(level, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 9, -3, 11, chunkBB);
        this.placeBlock(level, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 10, -3, 11, chunkBB);
        BlockState levers = Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACING, Direction.NORTH).setValue(LeverBlock.FACE, AttachFace.WALL);
        this.placeBlock(level, levers, 8, -3, 12, chunkBB);
        this.placeBlock(level, levers, 9, -3, 12, chunkBB);
        this.placeBlock(level, levers.setValue(LeverBlock.POWERED, true), 10, -3, 12, chunkBB);
        BlockState bars = Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true).setValue(IronBarsBlock.WEST, true);
        this.placeBlock(level, bars, 8, -2, 11, chunkBB);
        this.placeBlock(level, bars, 9, -2, 11, chunkBB);
        this.placeBlock(level, bars, 10, -2, 11, chunkBB);
        this.placeBlock(level, bars, 8, -1, 11, chunkBB);
        this.placeBlock(level, bars, 9, -1, 11, chunkBB);
        this.placeBlock(level, bars, 10, -1, 11, chunkBB);
        this.placeBlock(level, Blocks.AIR.defaultBlockState(), 9, 0, 8, chunkBB);
        this.placeBlock(level, Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.UP), 9, -2, 8, chunkBB);
        this.placeBlock(level, Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.WEST), 10, -2, 8, chunkBB);
        this.placeBlock(level, Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.WEST), 10, -1, 8, chunkBB);

        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 8, -3, 9, chunkBB);
        this.placeBlock(level, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10, -3, 9, chunkBB);
        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 8, -3, 8, chunkBB);
        this.placeBlock(level, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 8, chunkBB);
        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 10, -3, 8, chunkBB);
        this.placeBlock(level, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10, -1, 9, chunkBB);

        this.placeBlock(level, Blocks.REPEATER.defaultBlockState().setValue(RepeaterBlock.FACING, Direction.NORTH), 8, -3, 10, chunkBB);
        this.placeBlock(level, Blocks.REPEATER.defaultBlockState().setValue(RepeaterBlock.FACING, Direction.NORTH), 9, -3, 9, chunkBB);
        this.placeBlock(level, Blocks.REPEATER.defaultBlockState().setValue(RepeaterBlock.FACING, Direction.NORTH).setValue(RepeaterBlock.POWERED, true), 10, -3, 10, chunkBB);
        this.placeBlock(level, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 9, -3, 10, chunkBB);
        this.placeBlock(level, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE).setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 8, -2, 9, chunkBB);
        this.placeBlock(level, Blocks.REDSTONE_TORCH.defaultBlockState().setValue(RedstoneTorchBlock.LIT, false), 10, -2, 9, chunkBB);

        this.placeBlock(level, Blocks.AIR.defaultBlockState(), 9, -1, 7, chunkBB);
        this.createChest(level, chunkBB, random, 9, -2, 7, BuiltInLootTables.JUNGLE_TEMPLE);
        ci.cancel();
    }

}
