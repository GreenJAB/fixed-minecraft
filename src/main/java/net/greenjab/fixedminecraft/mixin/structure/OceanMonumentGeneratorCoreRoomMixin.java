package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.OceanMonumentGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@SuppressWarnings("unchecked")
@Mixin(OceanMonumentGenerator.CoreRoom.class)
public abstract class OceanMonumentGeneratorCoreRoomMixin {

    @ModifyArg(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/OceanMonumentGenerator$CoreRoom;fillWithOutline(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V", ordinal = 12), index = 8)
    private BlockState goldToDiamond(BlockState par9) {
        return Blocks.DIAMOND_BLOCK.getDefaultState();
    }
    @ModifyArg(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/OceanMonumentGenerator$CoreRoom;fillWithOutline(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V", ordinal = 12), index = 9)
    private BlockState goldToDiamond2(BlockState par9) {
        return Blocks.DIAMOND_BLOCK.getDefaultState();
    }

}
