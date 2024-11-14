package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.structure.MineshaftGenerator;
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


@Mixin(MineshaftGenerator.MineshaftRoom.class)
public abstract class MineshaftGeneratorMixin extends StructurePiece {
    protected MineshaftGeneratorMixin(StructurePieceType type, int length, BlockBox boundingBox) {
        super(type, length, boundingBox);
    }

    @Inject(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/MineshaftGenerator$MineshaftRoom;fillWithOutline(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V", ordinal = 0, shift = At.Shift.AFTER))
    private void fillStartingRoom(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator,
                                  Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot, CallbackInfo ci) {
        //this.addBlock(world, Blocks.DIAMOND_BLOCK.getDefaultState(), 0, 0, 0, chunkBox);
        //this.fill(world, )
        this.fillWithOutline(
                world,
                chunkBox,
                this.boundingBox.getMinX()+0,
                this.boundingBox.getMinY()+1 ,
                this.boundingBox.getMinZ()+2,
                this.boundingBox.getMinX()+4,
                this.boundingBox.getMinY()+1,
                this.boundingBox.getMinZ()+4,
                Blocks.BEACON.getDefaultState(),
                AIR,
                false
        );
        this.fillWithOutline(
                world,
                chunkBox,
                this.boundingBox.getMinX(),
                this.boundingBox.getMinY()+2 ,
                this.boundingBox.getMinZ()+3,
                this.boundingBox.getMinX()+4,
                this.boundingBox.getMinY()+2,
                this.boundingBox.getMinZ()+3,
                Blocks.BEACON.getDefaultState(),
                AIR,
                false
        );
        /*if (this.boundingBox.getBlockCountZ()>this.boundingBox.getBlockCountX()) {
            this.fillWithOutline(world,
                    chunkBox,
                    this.boundingBox.getMinX(),
                    this.boundingBox.getMinY(),
                    this.boundingBox.getMinZ() + 8,
                    this.boundingBox.getMinX() + 4,
                    this.boundingBox.getMinY(),
                    this.boundingBox.getMinZ() + 10,
                    Blocks.OAK_LOG.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.X),
                    Blocks.OAK_LOG.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.X),
                    false
            );
        } else {
            this.fillWithOutline(world,
                    chunkBox,
                    this.boundingBox.getMinX() + 8,
                    this.boundingBox.getMinY(),
                    this.boundingBox.getMinZ(),
                    this.boundingBox.getMinX() + 10,
                    this.boundingBox.getMinY(),
                    this.boundingBox.getMinZ() + 4,
                    Blocks.OAK_LOG.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Z),
                    Blocks.OAK_LOG.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Z),
                    false
            );
        }*/

        /*this.fillWithOutline(world,
                chunkBox,
                this.boundingBox.getMinX(),
                this.boundingBox.getMinY() +1 ,
                this.boundingBox.getMinZ()+1,
                this.boundingBox.getMinX()+2,
                this.boundingBox.getMinY() +1,
                this.boundingBox.getMinZ()+1,
                Blocks.OAK_LOG.getDefaultState(),
                AIR,
                false
        );*/

        /*this.fillWithOutline(world,
                chunkBox,
                this.boundingBox.getMinX(),
                this.boundingBox.getMinY() ,
                this.boundingBox.getMinZ()+2,
                this.boundingBox.getMinX()+3,
                this.boundingBox.getMinY() ,
                this.boundingBox.getMinZ()+4,
                Blocks.RAW_IRON_BLOCK.getDefaultState(),
                Blocks.DIAMOND_BLOCK.getDefaultState(),
                false
        );
        this.fillWithOutline(world,
                chunkBox,
                this.boundingBox.getMinX()+1,
                this.boundingBox.getMinY()+1 ,
                this.boundingBox.getMinZ()+3,
                this.boundingBox.getMinX()+2,
                this.boundingBox.getMinY()+1 ,
                this.boundingBox.getMinZ()+3,
                Blocks.RAW_IRON_BLOCK.getDefaultState(),
                Blocks.DIAMOND_BLOCK.getDefaultState(),
                false
        );*/
    }

}
