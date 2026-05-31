package net.greenjab.fixedminecraft.mixin.structure;

import net.greenjab.fixedminecraft.registry.registries.LootTableRegistry;
import net.minecraft.IdentifierException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;

@Mixin(WoodlandMansionStructure.class)
public abstract class WoodlandMansionStructureMixin {

    @Inject(method = "afterPlace", at = @At(value = "TAIL"))
    private void noEmptyChestsInWoodlandMansion(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                                         RandomSource random, BoundingBox chunkBB, ChunkPos chunkPos, PiecesContainer pieces,
                                         CallbackInfo ci) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BoundingBox boundingBox = pieces.calculateBoundingBox();

        for (int x = chunkBB.minX(); x <= chunkBB.maxX(); x++) {
            for (int y = chunkBB.minY(); y <= chunkBB.maxY(); y++) {
                for (int z = chunkBB.minZ(); z <= chunkBB.maxZ(); z++) {
                    pos.set(x, y, z);
                    if (level.getBlockState(pos).is(Blocks.TARGET)) {
                        if (level instanceof ServerLevel serverLevel) {
                            StructureTemplateManager manager = serverLevel.getStructureManager();
                            Optional<StructureTemplate> maybeStructureTemplate;
                            try {
                                maybeStructureTemplate = manager.get(Identifier.parse("minecraft:woodland_mansion/decorations/"+(random.nextInt(11))));
                                StructureTemplate structureTemplate = maybeStructureTemplate.get();
                                StructurePlaceSettings placeSettings = new StructurePlaceSettings().setKnownShape(false).setRotation(Rotation.getRandom(random));
                                structureTemplate.placeInWorld(level, pos, pos, placeSettings, StructureBlockEntity.createRandom(0), 2);
                            } catch (IdentifierException _) {
                            }
                        }
                    }
                    if (!level.isEmptyBlock(pos) && boundingBox.isInside(pos) && pieces.isInsidePiece(pos)) {
                        if (level.getBlockState(pos).is(Blocks.CHEST)) {
                            if (level.getBlockEntity(pos) instanceof ChestBlockEntity chestBlockEntity) {
                                if (chestBlockEntity.getLootTable()==null) {
                                    chestBlockEntity.setLootTable(LootTableRegistry.WOODLAND_MANSION_COMMON, random.nextLong());
                                }
                            }
                        }
                    }
                    if (level.getBlockState(pos).is(Blocks.DECORATED_POT)) {
                        if (level.getBlockEntity(pos) instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
                            if (decoratedPotBlockEntity.getLootTable()==null) {
                                decoratedPotBlockEntity.setLootTable(LootTableRegistry.WOODLAND_MANSION_POT, random.nextLong());
                            }
                        }
                    }
                }
            }
        }
    }
}
