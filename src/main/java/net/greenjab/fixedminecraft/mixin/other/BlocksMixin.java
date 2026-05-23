package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.block.NewPitcherPlantBlock;
import net.greenjab.fixedminecraft.registry.block.NewPackedIceBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import java.util.function.Function;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=packed_ice"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PACKED_ICE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block packedIce(String id, BlockBehaviour.Properties properties) {
        return register("packed_ice", NewPackedIceBlock::new, BlockBehaviour.Properties.of().randomTicks().mapColor(MapColor.ICE).instrument(NoteBlockInstrument.CHIME).friction(0.98F).strength(0.5F).sound(SoundType.GLASS));}

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=blue_ice"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;BLUE_ICE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block blueIce(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return register("blue_ice", NewBlueIceBlock::new, BlockBehaviour.Properties.of().randomTicks().mapColor(MapColor.ICE).strength(2.8F).friction(0.989F).sound(SoundType.GLASS));}

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=snow"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;SNOW:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block snow(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return register("snow", NewSnowBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).replaceable().forceSolidOff().randomTicks().strength(0.1F).requiresCorrectToolForDrops().sound(SoundType.SNOW).isViewBlocking((state, _, _) -> state.getValue(SnowLayerBlock.LAYERS) >= 8).pushReaction(PushReaction.DESTROY));}

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=amethyst_block"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;AMETHYST_BLOCK:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block powerAmethystBlock(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return register("amethyst_block", NewAmethystBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops());}

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=torchflower"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;TORCHFLOWER:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block newTorchFlower(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return register("torchflower", new_properties -> new NewTorchFlowerBlock(MobEffects.NIGHT_VISION, 5.0F, new_properties),
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).noCollision().instabreak()
                        .sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ)
                        .pushReaction(PushReaction.DESTROY).lightLevel(_ -> 13));}

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=pitcher_crop"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PITCHER_CROP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block newPitcherCrop(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return register("pitcher_crop", NewPitcherCropBlock::new,
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).noCollision().instabreak()
                        .randomTicks().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));}

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=pitcher_plant"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PITCHER_PLANT:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block newPitcherPod(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return register("pitcher_plant", NewPitcherPlantBlock::new,
                BlockBehaviour.Properties.of().mapColor(DyeColor.CYAN).noCollision().instabreak().sound(SoundType.CROP)
                        .offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));}

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from = @At(
            value = "CONSTANT", args = "stringValue=fletching_table"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;FLETCHING_TABLE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static Block newFletchingTable(String id, BlockBehaviour.Properties properties) {
        return register("fletching_table", NewFletchingTableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());}

    @Unique
    private static Block register(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return register(keyOf(id), factory, properties);
    }
    @Unique
    private static ResourceKey<Block> keyOf(String id) {
        return ResourceKey.create(Registries.BLOCK, Identifier.withDefaultNamespace(id));
    }
    @Unique
    private static Block register(ResourceKey<Block> key, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        Block block = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=wither_rose"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;WITHER_ROSE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor witherRoseMapColor(MapColor color) {return DyeColor.BLACK.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=cornflower"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;CORNFLOWER:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor cornflowerMapColor(MapColor color) {return DyeColor.BLUE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=blue_orchid"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;BLUE_ORCHID:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor blueOrchidMapColor(MapColor color) {return DyeColor.LIGHT_BLUE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=azure_bluet"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;AZURE_BLUET:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor azureMapColor(MapColor color) {return DyeColor.LIGHT_GRAY.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=oxeye_daisy"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;OXEYE_DAISY:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor oxeyeMapColor(MapColor color) {return DyeColor.LIGHT_GRAY.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=white_tulip"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;WHITE_TULIP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor whiteTulipMapColor(MapColor color) {return DyeColor.LIGHT_GRAY.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=allium"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;ALLIUM:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor alliumMapColor(MapColor color) {return DyeColor.MAGENTA.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=lilac"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;LILAC:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor lilacMapColor(MapColor color) {return DyeColor.MAGENTA.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=orange_tulip"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;ORANGE_TULIP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor orangeTulipMapColor(MapColor color) {return DyeColor.ORANGE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=pink_tulip"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PINK_TULIP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor pinkTulipMapColor(MapColor color) {return DyeColor.PINK.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=peony"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PEONY:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor peonyMapColor(MapColor color) {return DyeColor.PINK.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=poppy"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;POPPY:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor poppyMapColor(MapColor color) {return DyeColor.RED.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=red_tulip"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;RED_TULIP:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor redTulipMapColor(MapColor color) {return DyeColor.RED.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=rose_bush"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;ROSE_BUSH:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor roseBushMapColor(MapColor color) {return DyeColor.RED.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=lily_of_the_valley"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;LILY_OF_THE_VALLEY:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor lilyMapColor(MapColor color) {return DyeColor.WHITE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=dandelion"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;DANDELION:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor dandelionMapColor(MapColor color) {return DyeColor.YELLOW.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=golden_dandelion"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;GOLDEN_DANDELION:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor goldenDandelionMapColor(MapColor color) {return MapColor.GOLD;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=wildflowers"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;WILDFLOWERS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor wildflowersMapColor(MapColor color) {return DyeColor.YELLOW.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=sunflower"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;SUNFLOWER:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor sunflowerMapColor(MapColor color) {return DyeColor.YELLOW.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=pink_petals"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;PINK_PETALS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor pinkPetalsMapColor(MapColor color) {return MapColor.TERRACOTTA_WHITE;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=short_grass"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;SHORT_GRASS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor clearGrassMapColor(MapColor color) {return MapColor.NONE;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=tall_grass"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;TALL_GRASS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor clearTallGrassMapColor(MapColor color) {return MapColor.NONE;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;mapColor(Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=tinted_glass"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;TINTED_GLASS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)))
    private static MapColor clearTintedGlassMapColor(MapColor color) {return MapColor.NONE;}


    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=rail"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;RAIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static BlockBehaviour.Properties railMapColor(BlockBehaviour.Properties properties) {return properties.mapColor(MapColor.METAL);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=powered_rail"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;POWERED_RAIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static BlockBehaviour.Properties powerRailMapColor(BlockBehaviour.Properties properties) {return properties.mapColor(MapColor.METAL);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=detector_rail"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;DETECTOR_RAIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static BlockBehaviour.Properties detectorRailMapColor(BlockBehaviour.Properties properties) {return properties.mapColor(MapColor.METAL);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), slice = @Slice( from =
    @At(value = "CONSTANT", args = "stringValue=activator_rail"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/block/Blocks;ACTIVATOR_RAIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static BlockBehaviour.Properties activatorRailMapColor(BlockBehaviour.Properties properties) {return properties.mapColor(MapColor.METAL);}
}
