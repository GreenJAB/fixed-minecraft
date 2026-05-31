package net.greenjab.fixedminecraft.mixin.structure;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WoodlandMansionPieces.MansionPiecePlacer.class)
public abstract class WoodlandMansionPiecePlacerMixin {

    @Shadow
    @Final
    private StructureTemplateManager structureTemplateManager;

    @Shadow
    @Final
    private RandomSource random;

    @Inject(method = "createMansion", at = @At(value = "INVOKE",
                                               target = "Lnet/minecraft/world/level/levelgen/structure/structures/WoodlandMansionPieces$SimpleGrid;get(II)I", ordinal =1
    ))
    private void noEmptyChestsInWoodlandMansion(BlockPos origin, Rotation rotation, List<WoodlandMansionPieces.WoodlandMansionPiece> pieces,
                                                WoodlandMansionPieces.MansionGrid mansion, CallbackInfo ci, @Local(ordinal = 2) BlockPos pos) {
        Vector3i rot = rotation.rotation().rotate(new Vector3i(6, 0, 6));
        System.out.println(rotation.rotation() + ", " + rot);
        if (this.random.nextInt(3)==0) pieces.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "decorations/deco", pos, Rotation.getRandom(this.random)));
        if (this.random.nextInt(3)==0) pieces.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "decorations/deco", pos.offset(rot.x, 0, 0), Rotation.getRandom(this.random)));
        if (this.random.nextInt(3)==0) pieces.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "decorations/deco", pos.offset(rot.x, 0, rot.z), Rotation.getRandom(this.random)));
        if (this.random.nextInt(3)==0) pieces.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "decorations/deco", pos.offset(0, 0, rot.z), Rotation.getRandom(this.random)));

        if (this.random.nextInt(3)==0) pieces.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "decorations/illager", pos.offset(rot.x/2, 2, rot.z/2), Rotation.getRandom(this.random)));

    }
}
