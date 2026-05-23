package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;


@Mixin(StrongholdPieces.Library.class)
public abstract class StrongholdPieceLibraryMixin extends StructurePiece {

    protected StrongholdPieceLibraryMixin(StructurePieceType type, int length, BoundingBox boundingBox) {
        super(type, length, boundingBox);
    }

    @Inject(method = "postProcess", at = @At(value = "TAIL"))
    private void EnchantingTableSetpiece(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                                         RandomSource random, BoundingBox chunkBB, ChunkPos chunkPos, BlockPos referencePos, CallbackInfo ci) {

        this.generateAirBox(level, chunkBB, 9,1, 5, 11, 3,9);
        this.placeBlock(level, Blocks.OBSIDIAN.defaultBlockState(), 10, 0, 7, chunkBB);
        this.generateBox(level, chunkBB, 9, 1, 9, 11, 2, 9, Blocks.BOOKSHELF.defaultBlockState(), Blocks.CHISELED_BOOKSHELF.defaultBlockState(), false);
        this.generateBox(level, chunkBB, 9, 1, 5, 11, 2, 5, Blocks.BOOKSHELF.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.placeBlock(level, Blocks.AIR.defaultBlockState(), 9, 2, 5, chunkBB);
        this.placeBlock(level, Blocks.AIR.defaultBlockState(), 9, 2, 9, chunkBB);

        placeChiseledBookshelf(level, random, 10, 1, 9, Direction.SOUTH, chunkBB);
        placeChiseledBookshelf(level, random, 9, 1, 5, Direction.NORTH, chunkBB);
        placeChiseledBookshelf(level, random, 12, 2, 8, Direction.WEST, chunkBB);
        placeChiseledBookshelf(level, random, 12, 1, 6, Direction.WEST, chunkBB);

    }

    @Unique
    private void placeChiseledBookshelf(WorldGenLevel world, RandomSource random, int x, int y, int z, Direction facing, BoundingBox bb) {
        BlockState state = Blocks.CHISELED_BOOKSHELF.defaultBlockState()
                .setValue(ChiseledBookShelfBlock.FACING, facing);
        if (random.nextBoolean()) {
            this.placeBlock(world, state, x, y, z, bb);
            return;
        }
        int bookSlot = random.nextInt(6);
        state = state.setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(bookSlot), true);

        this.placeBlock(world, state, x, y, z, bb);

        BlockPos worldPos = this.getWorldPos(x, y, z);
        if (bb.isInside(worldPos) && world.getBlockEntity(worldPos) instanceof ChiseledBookShelfBlockEntity shelf) {
            shelf.setItemNoUpdate(bookSlot, createEnchantedBook(world, random));
        }
    }

    @Unique
    private static ItemStack createEnchantedBook(WorldGenLevel level, RandomSource random) {
        Optional<HolderSet.Named<Enchantment>> enchants = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.ON_RANDOM_LOOT);
        ItemStack book = new ItemStack(Items.BOOK);
        book = EnchantmentHelper.enchantItem(random, book, 8, level.registryAccess(), enchants);
        return book;
    }
}
