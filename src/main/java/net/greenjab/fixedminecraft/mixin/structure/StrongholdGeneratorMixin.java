package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.loot.LootTables;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.ShiftableStructurePiece;
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

        /*this.addBlock(world, Blocks.REDSTONE_WALL_TORCH.getDefaultState().with(RedstoneTorchBlock.LIT, true).with(WallTorchBlock.FACING, Direction.EAST), 8, -1, 10, chunkBox);
        this.addBlock(world, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 8, -3, 11, chunkBox);
        this.addBlock(world, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 9, -3, 11, chunkBox);
        this.addBlock(world, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 10, -3, 11, chunkBox);
        BlockState levers = Blocks.LEVER.getDefaultState().with(LeverBlock.FACING, Direction.NORTH).with(LeverBlock.FACE, BlockFace.WALL);
        this.addBlock(world, levers, 8, -3, 12, chunkBox);
        this.addBlock(world, levers, 9, -3, 12, chunkBox);
        this.addBlock(world, levers.with(LeverBlock.POWERED, true), 10, -3, 12, chunkBox);
        BlockState bars = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true);
        this.addBlock(world, bars, 8, -2, 11, chunkBox);
        this.addBlock(world, bars, 9, -2, 11, chunkBox);
        this.addBlock(world, bars, 10, -2, 11, chunkBox);
        this.addBlock(world, bars, 8, -1, 11, chunkBox);
        this.addBlock(world, bars, 9, -1, 11, chunkBox);
        this.addBlock(world, bars, 10, -1, 11, chunkBox);
        this.addBlock(world, Blocks.AIR.getDefaultState(), 9, 0, 8, chunkBox);
        this.addBlock(world, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.UP), 9, -2, 8, chunkBox);
        this.addBlock(world, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -2, 8, chunkBox);
        this.addBlock(world, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -1, 8, chunkBox);

        this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 8, -3, 9, chunkBox);
        this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -3, 9, chunkBox);
        this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 8, -3, 8, chunkBox);
        this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 8, chunkBox);
        this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 10, -3, 8, chunkBox);
        this.addBlock(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -1, 9, chunkBox);

        this.addBlock(world, Blocks.REPEATER.getDefaultState().with(RepeaterBlock.FACING, Direction.NORTH), 8, -3, 10, chunkBox);
        this.addBlock(world, Blocks.REPEATER.getDefaultState().with(RepeaterBlock.FACING, Direction.NORTH), 9, -3, 9, chunkBox);
        this.addBlock(world, Blocks.REPEATER.getDefaultState().with(RepeaterBlock.FACING, Direction.NORTH).with(RepeaterBlock.POWERED, true), 10, -3, 10, chunkBox);
        this.addBlock(world, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE), 9, -3, 10, chunkBox);
        this.addBlock(world, Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.SIDE).with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE), 8, -2, 9, chunkBox);
        this.addBlock(world, Blocks.REDSTONE_TORCH.getDefaultState().with(RedstoneTorchBlock.LIT, false), 10, -2, 9, chunkBox);

        this.addBlock(world, Blocks.AIR.getDefaultState(), 9, -1, 7, chunkBox);
        this.addChest(world, chunkBox, random, 9, -2, 7, LootTables.JUNGLE_TEMPLE_CHEST);//*/
    }

}
