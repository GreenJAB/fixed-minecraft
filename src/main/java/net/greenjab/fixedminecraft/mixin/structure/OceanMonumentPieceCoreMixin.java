package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(OceanMonumentPieces.OceanMonumentCoreRoom.class)
public abstract class OceanMonumentPieceCoreMixin {

    @ModifyArg(method = "postProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/structures/OceanMonumentPieces$OceanMonumentCoreRoom;generateBox(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;IIIIIILnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Z)V", ordinal = 12), index = 8)
    private BlockState goldToDiamond(BlockState par9) {
        return Blocks.DIAMOND_BLOCK.defaultBlockState();
    }
    @ModifyArg(method = "postProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/structures/OceanMonumentPieces$OceanMonumentCoreRoom;generateBox(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;IIIIIILnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Z)V", ordinal = 12), index = 9)
    private BlockState goldToDiamond2(BlockState par9) {
        return Blocks.DIAMOND_BLOCK.defaultBlockState();
    }

}
