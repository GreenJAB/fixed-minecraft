package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.block.NewPitcherPlantBlock;
import net.greenjab.fixedminecraft.registry.block.NewPackedIceBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.greenjab.fixedminecraft.registry.block.NewBlueIceBlock;
import net.greenjab.fixedminecraft.registry.block.NewSnowBlock;
import net.greenjab.fixedminecraft.registry.block.NewAmethystBlock;
import net.greenjab.fixedminecraft.registry.block.NewTorchFlowerBlock;
import net.greenjab.fixedminecraft.registry.block.NewPitcherCropBlock;
import net.greenjab.fixedminecraft.registry.block.NewFletchingTableBlock;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import java.util.function.Function;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;PACKED_ICE:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PACKED_ICE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block packedIce(BlockItemId id, BlockBehaviour.Properties properties, Operation<Block> original) {
        return register(id, NewPackedIceBlock::new, BlockBehaviour.Properties.of().randomTicks().mapColor(MapColor.ICE).instrument(NoteBlockInstrument.CHIME).friction(0.98F).strength(0.5F).sound(SoundType.GLASS));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;BLUE_ICE:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;BLUE_ICE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block blueIce(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties, Operation<Block> original) {
        return register(id, NewBlueIceBlock::new, BlockBehaviour.Properties.of().randomTicks().mapColor(MapColor.ICE).strength(2.8F).friction(0.989F).sound(SoundType.GLASS));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;SNOW:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;SNOW:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block snow(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties, Operation<Block> original) {
        return register(id, NewSnowBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).replaceable().forceSolidOff().randomTicks().strength(0.1F).requiresCorrectToolForDrops().sound(SoundType.SNOW).isViewBlocking( (state, _, _, _) -> state.getValue(SnowLayerBlock.LAYERS) >= 8).pushReaction(PushReaction.POPPED));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;AMETHYST_BLOCK:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;AMETHYST_BLOCK:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block powerAmethystBlock(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties, Operation<Block> original) {
        return register(id, NewAmethystBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops());}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;TORCHFLOWER:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;TORCHFLOWER:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block newTorchFlower(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties, Operation<Block> original) {
        return register(id, new_properties -> new NewTorchFlowerBlock(MobEffects.NIGHT_VISION, 5.0F, new_properties),
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).noCollision().instabreak()
                        .sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ)
                        .pushReaction(PushReaction.POPPED).lightLevel(_ -> 13));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;PITCHER_CROP:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PITCHER_CROP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block newPitcherCrop(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties, Operation<Block> original) {
        return register(id, NewPitcherCropBlock::new,
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).noCollision().instabreak()
                        .randomTicks().sound(SoundType.CROP).pushReaction(PushReaction.POPPED));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;PITCHER_PLANT:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PITCHER_PLANT:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block newPitcherPod(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties,
                                       Operation<Block> original) {
        return register(id, NewPitcherPlantBlock::new,
                BlockBehaviour.Properties.of().mapColor(DyeColor.CYAN).noCollision().instabreak().sound(SoundType.CROP)
                        .offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.POPPED));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;FLETCHING_TABLE:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;FLETCHING_TABLE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block newFletchingTable(BlockItemId id, BlockBehaviour.Properties properties, Operation<Block> original) {
        return register(id, NewFletchingTableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());}

    @Unique private static Block register(BlockItemId id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return register(id.block(), factory, properties);
    }

    @Unique private static Block register(ResourceKey<Block> key, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        Block block = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;WITHER_ROSE:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;WITHER_ROSE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor witherRoseMapColor(MapColor color) {return DyeColor.BLACK.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;CORNFLOWER:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;CORNFLOWER:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor cornflowerMapColor(MapColor color) {return DyeColor.BLUE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;BLUE_ORCHID:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;BLUE_ORCHID:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor blueOrchidMapColor(MapColor color) {return DyeColor.LIGHT_BLUE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;AZURE_BLUET:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;AZURE_BLUET:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor azureMapColor(MapColor color) {return DyeColor.LIGHT_GRAY.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;OXEYE_DAISY:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;OXEYE_DAISY:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor oxeyeMapColor(MapColor color) {return DyeColor.LIGHT_GRAY.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;WHITE_TULIP:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;WHITE_TULIP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor whiteTulipMapColor(MapColor color) {return DyeColor.LIGHT_GRAY.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;ALLIUM:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;ALLIUM:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor alliumMapColor(MapColor color) {return DyeColor.MAGENTA.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;LILAC:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;LILAC:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor lilacMapColor(MapColor color) {return DyeColor.MAGENTA.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;ORANGE_TULIP:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;ORANGE_TULIP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor orangeTulipMapColor(MapColor color) {return DyeColor.ORANGE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;PINK_TULIP:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PINK_TULIP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor pinkTulipMapColor(MapColor color) {return DyeColor.PINK.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;PEONY:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PEONY:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor peonyMapColor(MapColor color) {return DyeColor.PINK.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;POPPY:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;POPPY:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor poppyMapColor(MapColor color) {return DyeColor.RED.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;RED_TULIP:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;RED_TULIP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor redTulipMapColor(MapColor color) {return DyeColor.RED.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;ROSE_BUSH:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;ROSE_BUSH:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor roseBushMapColor(MapColor color) {return DyeColor.RED.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;LILY_OF_THE_VALLEY:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;LILY_OF_THE_VALLEY:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor lilyMapColor(MapColor color) {return DyeColor.WHITE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;DANDELION:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;DANDELION:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor dandelionMapColor(MapColor color) {return DyeColor.YELLOW.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;GOLDEN_DANDELION:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;GOLDEN_DANDELION:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor goldenDandelionMapColor(MapColor color) {return MapColor.GOLD;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;WILDFLOWERS:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;WILDFLOWERS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor wildflowersMapColor(MapColor color) {return DyeColor.YELLOW.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;SUNFLOWER:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;SUNFLOWER:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor sunflowerMapColor(MapColor color) {return DyeColor.YELLOW.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;PINK_PETALS:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PINK_PETALS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor pinkPetalsMapColor(MapColor color) {return MapColor.TERRACOTTA_WHITE;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;SHORT_GRASS:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;SHORT_GRASS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor clearGrassMapColor(MapColor color) {return MapColor.NONE;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;TALL_GRASS:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;TALL_GRASS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor clearTallGrassMapColor(MapColor color) {return MapColor.NONE;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;TINTED_GLASS:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;TINTED_GLASS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor clearTintedGlassMapColor(MapColor color) {return MapColor.NONE;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;REDSTONE_LAMP:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;REDSTONE_LAMP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static BlockBehaviour.Properties LampColor(BlockBehaviour.Properties properties) {return properties.mapColor(state -> state.getValue(RedstoneLampBlock.LIT)?MapColor.COLOR_YELLOW:MapColor.TERRACOTTA_ORANGE);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;RAIL:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;RAIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static BlockBehaviour.Properties railMapColor(BlockBehaviour.Properties properties) {return properties.mapColor(MapColor.METAL);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;POWERED_RAIL:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;POWERED_RAIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static BlockBehaviour.Properties powerRailMapColor(BlockBehaviour.Properties properties) {return properties.mapColor(MapColor.METAL);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;DETECTOR_RAIL:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;DETECTOR_RAIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static BlockBehaviour.Properties detectorRailMapColor(BlockBehaviour.Properties properties) {return properties.mapColor(MapColor.METAL);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;ACTIVATOR_RAIL:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;ACTIVATOR_RAIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static BlockBehaviour.Properties activatorRailMapColor(BlockBehaviour.Properties properties) {return properties.mapColor(MapColor.METAL);}
}
