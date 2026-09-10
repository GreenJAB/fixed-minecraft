package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import java.util.ArrayList;
import java.util.Arrays;

@Mixin(BlockEntityTypes.class)
public abstract class BlockEntityTypeMixin{

    @ModifyArg(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntityTypes;register(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/block/entity/BlockEntityType$BlockEntitySupplier;[Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/entity/BlockEntityType;", ordinal = 0), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BlockEntityTypeIds;SIGN:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/entity/BlockEntityTypes;SIGN:Lnet/minecraft/world/level/block/entity/BlockEntityType;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static Block[] sign(Block[] validBlocks) {
        ArrayList<Block> newBlocks = new ArrayList<>(Arrays.asList(validBlocks));
        newBlocks.add(BlockRegistry.AZALEA_SIGN);
        newBlocks.add(BlockRegistry.AZALEA_WALL_SIGN);
        Block[] newBlocksArray = new Block[newBlocks.size()];
        for (int i = 0;i<newBlocksArray.length;i++) { newBlocksArray[i]=newBlocks.get(i); }
        return newBlocksArray;
    }

    @ModifyArg(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntityTypes;register(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/block/entity/BlockEntityType$BlockEntitySupplier;[Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/entity/BlockEntityType;", ordinal = 0), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BlockEntityTypeIds;HANGING_SIGN:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/entity/BlockEntityTypes;HANGING_SIGN:Lnet/minecraft/world/level/block/entity/BlockEntityType;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static Block[] hanging_sign(Block[] validBlocks) {
        ArrayList<Block> newBlocks = new ArrayList<>(Arrays.asList(validBlocks));
        newBlocks.add(BlockRegistry.AZALEA_HANGING_SIGN);
        newBlocks.add(BlockRegistry.AZALEA_WALL_HANGING_SIGN);
        Block[] newBlocksArray = new Block[newBlocks.size()];
        for (int i = 0;i<newBlocksArray.length;i++) { newBlocksArray[i]=newBlocks.get(i); }
        return newBlocksArray;
    }

     @ModifyArg(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntityTypes;register(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/block/entity/BlockEntityType$BlockEntitySupplier;[Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/entity/BlockEntityType;", ordinal = 0), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BlockEntityTypeIds;SHELF:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/entity/BlockEntityTypes;SHELF:Lnet/minecraft/world/level/block/entity/BlockEntityType;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static Block[] shelf(Block[] validBlocks) {
        ArrayList<Block> newBlocks = new ArrayList<>(Arrays.asList(validBlocks));
        newBlocks.add(BlockRegistry.AZALEA_SHELF);
        Block[] newBlocksArray = new Block[newBlocks.size()];
        for (int i = 0;i<newBlocksArray.length;i++) { newBlocksArray[i]=newBlocks.get(i); }
        return newBlocksArray;
    }
}
