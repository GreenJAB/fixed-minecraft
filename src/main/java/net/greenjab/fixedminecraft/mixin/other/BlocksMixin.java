package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.block.NewIceBlock;
import net.greenjab.fixedminecraft.registry.block.NewPitcherPlantBlock;
import net.greenjab.fixedminecraft.registry.block.PackedIceBlock;
import net.greenjab.fixedminecraft.registry.block.BlueIceBlock;
import net.greenjab.fixedminecraft.registry.block.NewSnowBlock;
import net.greenjab.fixedminecraft.registry.block.NewAmethystBlock;
import net.greenjab.fixedminecraft.registry.block.NewDaylightDetectorBlock;
import net.greenjab.fixedminecraft.registry.block.NewTorchFlowerBlock;
import net.greenjab.fixedminecraft.registry.block.NewPitcherCropBlock;
import net.greenjab.fixedminecraft.registry.block.FletchingTableBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import net.minecraft.block.CropBlock;
import net.minecraft.block.FlowerbedBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

import static net.minecraft.block.Blocks.IRON_BLOCK;

@Mixin(Blocks.class)
public class BlocksMixin {

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;SNOW:Lnet/minecraft/block/Block;")))
    private static Block ice(String id,  Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register("ice", NewIceBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.PALE_PURPLE).slipperiness(0.98F).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(/* method_26132 */ (state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR).solidBlock(Blocks::never));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;COAL_BLOCK:Lnet/minecraft/block/Block;")))
    private static Block packedIce(String id, AbstractBlock.Settings settings) {
        return register("packed_ice", PackedIceBlock::new, AbstractBlock.Settings.create().ticksRandomly().mapColor(MapColor.PALE_PURPLE).instrument(NoteBlockInstrument.CHIME).slipperiness(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                           target = "Lnet/minecraft/block/Blocks;SEA_PICKLE:Lnet/minecraft/block/Block;")))
    private static Block blueIce(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register("blue_ice", BlueIceBlock::new, AbstractBlock.Settings.create().ticksRandomly().mapColor(MapColor.PALE_PURPLE).strength(2.8F).slipperiness(0.989F).sounds(BlockSoundGroup.GLASS));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                     target = "Lnet/minecraft/block/Blocks;STONE_BUTTON:Lnet/minecraft/block/Block;")))
    private static Block snow(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register("snow", NewSnowBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.WHITE).replaceable().notSolid().ticksRandomly().strength(0.1F).requiresTool().sounds(BlockSoundGroup.SNOW).blockVision(/* method_39537 */ (state, world, pos) -> (Integer)state.get(SnowBlock.LAYERS) >= 8).pistonBehavior(PistonBehavior.DESTROY));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;BLACK_CANDLE_CAKE:Lnet/minecraft/block/Block;")))
    private static Block powerAmethystBlock(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register("amethyst_block", NewAmethystBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).strength(1.5F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool());
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                                                                                                                                                                                                                                                                                target = "Lnet/minecraft/block/Blocks;COMPARATOR:Lnet/minecraft/block/Block;")))
    private static Block compOutputDaylightDetector(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register("daylight_detector", NewDaylightDetectorBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable());
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                               target = "Lnet/minecraft/block/Blocks;DANDELION:Lnet/minecraft/block/Block;")))
    private static Block newTorchFlower(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings2) {
        return register(
                "torchflower",
                /* method_63437 */ settings -> new NewTorchFlowerBlock(StatusEffects.NIGHT_VISION, 5.0F, settings),
                AbstractBlock.Settings.create()
                        .mapColor(MapColor.ORANGE)
                        .noCollision()
                        .breakInstantly()
                        .sounds(BlockSoundGroup.GRASS)
                        .offset(AbstractBlock.OffsetType.XZ)
                        .pistonBehavior(PistonBehavior.DESTROY)
                        .luminance( state -> 13)
        );
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                         target = "Lnet/minecraft/block/Blocks;TORCHFLOWER_CROP:Lnet/minecraft/block/Block;")))
    private static Block newPitcherCrop(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register("pitcher_crop",
                NewPitcherCropBlock::new,
                AbstractBlock.Settings.create()
                        .mapColor(MapColor.CYAN)
                        .noCollision()
                        .ticksRandomly()
                        .breakInstantly()
                        .sounds(BlockSoundGroup.CROP)
                        .pistonBehavior(PistonBehavior.DESTROY));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                       target = "Lnet/minecraft/block/Blocks;PITCHER_CROP:Lnet/minecraft/block/Block;")))
    private static Block newPitcherPod(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register("pitcher_plant",
                NewPitcherPlantBlock::new,
                AbstractBlock.Settings.create()
                        .mapColor(DyeColor.CYAN)
                        .noCollision()
                        .breakInstantly()
                        .sounds(BlockSoundGroup.CROP)
                        .offset(AbstractBlock.OffsetType.XZ)
                        .burnable()
                        .pistonBehavior(PistonBehavior.DESTROY)
        );
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                          target = "Lnet/minecraft/block/Blocks;CARTOGRAPHY_TABLE:Lnet/minecraft/block/Block;")))
    private static Block newFletchingTable(String id, AbstractBlock.Settings settings) {
        return register(
                "fletching_table",
                FletchingTableBlock::new,
                AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable()
        );
    }

    @Unique
    private static Block register(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return register(keyOf(id), factory, settings);
    }
    @Unique
    private static RegistryKey<Block> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla(id));
    }
    @Unique
    private static Block register(RegistryKey<Block> key, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Block block = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }


    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;CORNFLOWER:Lnet/minecraft/block/Block;")))
    private static MapColor witherRoseMapColor(MapColor color) {return DyeColor.BLACK.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;OXEYE_DAISY:Lnet/minecraft/block/Block;")))
    private static MapColor cornflowerMapColor(MapColor color) {return DyeColor.BLUE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;POPPY:Lnet/minecraft/block/Block;")))
    private static MapColor blueOrchidMapColor(MapColor color) {return DyeColor.LIGHT_BLUE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;ALLIUM:Lnet/minecraft/block/Block;")))
    private static MapColor azureMapColor(MapColor color) {return DyeColor.LIGHT_GRAY.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;PINK_TULIP:Lnet/minecraft/block/Block;")))
    private static MapColor oxeyeMapColor(MapColor color) {return DyeColor.LIGHT_GRAY.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;ORANGE_TULIP:Lnet/minecraft/block/Block;")))
    private static MapColor whiteTulipMapColor(MapColor color) {return DyeColor.LIGHT_GRAY.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;BLUE_ORCHID:Lnet/minecraft/block/Block;")))
    private static MapColor alliumMapColor(MapColor color) {return DyeColor.MAGENTA.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;SUNFLOWER:Lnet/minecraft/block/Block;")))
    private static MapColor lilacMapColor(MapColor color) {return DyeColor.MAGENTA.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;RED_TULIP:Lnet/minecraft/block/Block;")))
    private static MapColor orangeTulipMapColor(MapColor color) {return DyeColor.ORANGE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;DANDELION:Lnet/minecraft/block/Block;")))
    private static MapColor torchFlowerMapColor(MapColor color) {return DyeColor.ORANGE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;WHITE_TULIP:Lnet/minecraft/block/Block;")))
    private static MapColor pinkTulipMapColor(MapColor color) {return DyeColor.PINK.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;ROSE_BUSH:Lnet/minecraft/block/Block;")))
    private static MapColor peonyMapColor(MapColor color) {return DyeColor.PINK.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;TORCHFLOWER:Lnet/minecraft/block/Block;")))
    private static MapColor poppyMapColor(MapColor color) {return DyeColor.RED.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;AZURE_BLUET:Lnet/minecraft/block/Block;")))
    private static MapColor redTulipMapColor(MapColor color) {return DyeColor.RED.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;LILAC:Lnet/minecraft/block/Block;")))
    private static MapColor roseBushMapColor(MapColor color) {return DyeColor.RED.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;WITHER_ROSE:Lnet/minecraft/block/Block;")))
    private static MapColor lilyMapColor(MapColor color) {return DyeColor.WHITE.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;MOVING_PISTON:Lnet/minecraft/block/Block;")))
    private static MapColor dandelionMapColor(MapColor color) {return DyeColor.YELLOW.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;PINK_PETALS:Lnet/minecraft/block/Block;")))
    private static MapColor wildflowersMapColor(MapColor color) {return DyeColor.YELLOW.getMapColor();}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;PACKED_ICE:Lnet/minecraft/block/Block;")))
    private static MapColor sunflowerMapColor(MapColor color) {return DyeColor.YELLOW.getMapColor();}




    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;MOSS_CARPET:Lnet/minecraft/block/Block;")))
    private static MapColor pinkPetalsMapColor(MapColor color) {return MapColor.TERRACOTTA_WHITE;}




    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;COBWEB:Lnet/minecraft/block/Block;")))
    private static MapColor clearGrassMapColor(MapColor color) {return MapColor.CLEAR;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;PEONY:Lnet/minecraft/block/Block;")))
    private static MapColor clearTallGrassMapColor(MapColor color) {return MapColor.CLEAR;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/AbstractBlock$Settings;mapColor(Lnet/minecraft/block/MapColor;)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;CALCITE:Lnet/minecraft/block/Block;")))
    private static MapColor clearTintedGlassMapColor(MapColor color) {return MapColor.CLEAR;}




    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;LADDER:Lnet/minecraft/block/Block;")), index = 2)
    private static AbstractBlock.Settings railMapColor(AbstractBlock.Settings settings) {return settings.mapColor(MapColor.IRON_GRAY);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;BLACK_BED:Lnet/minecraft/block/Block;")), index = 2)
    private static AbstractBlock.Settings powerrailMapColor(AbstractBlock.Settings settings) {return settings.mapColor(MapColor.IRON_GRAY);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;POWERED_RAIL:Lnet/minecraft/block/Block;")), index = 2)
    private static AbstractBlock.Settings detectorrailMapColor(AbstractBlock.Settings settings) {return settings.mapColor(MapColor.IRON_GRAY);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE",
                                            target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from =
    @At( value = "FIELD",target = "Lnet/minecraft/block/Blocks;QUARTZ_STAIRS:Lnet/minecraft/block/Block;")), index = 2)
    private static AbstractBlock.Settings activatorrailMapColor(AbstractBlock.Settings settings) {return settings.mapColor(MapColor.IRON_GRAY);}


}
