package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.block.NewIceBlock;
import net.greenjab.fixedminecraft.registry.block.NewPitcherPlantBlock;
import net.greenjab.fixedminecraft.registry.block.PackedIceBlock;
import net.greenjab.fixedminecraft.registry.block.BlueIceBlock;
import net.greenjab.fixedminecraft.registry.block.NewAmethystBlock;
import net.greenjab.fixedminecraft.registry.block.NewDaylightDetectorBlock;
import net.greenjab.fixedminecraft.registry.block.NewTorchFlowerBlock;
import net.greenjab.fixedminecraft.registry.block.NewPitcherCropBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AmethystBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.IceBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public class BlocksMixin {

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=ice"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/IceBlock;", ordinal = 0))
    private static IceBlock ice(AbstractBlock.Settings settings) {
        return new NewIceBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_PURPLE).slipperiness(0.98F).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning( (state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR).solidBlock(Blocks::never));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=packed_ice"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0))
    private static Block packedIce(AbstractBlock.Settings settings) {
        return new PackedIceBlock(AbstractBlock.Settings.create().ticksRandomly().mapColor(MapColor.PALE_PURPLE).instrument(NoteBlockInstrument.CHIME).slipperiness(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=blue_ice"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/TranslucentBlock;", ordinal = 0))
    private static TranslucentBlock blueIce(AbstractBlock.Settings settings) {
        return new BlueIceBlock(AbstractBlock.Settings.create().ticksRandomly().mapColor(MapColor.PALE_PURPLE).strength(2.8F).slipperiness(0.989F).sounds(BlockSoundGroup.GLASS));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=amethyst_block"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/AmethystBlock;", ordinal = 0))
    private static AmethystBlock powerAmethystBlock(AbstractBlock.Settings settings) {
        return new NewAmethystBlock(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).strength(1.5F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool());
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=daylight_detector"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/DaylightDetectorBlock;", ordinal = 0))
    private static DaylightDetectorBlock compOutputDaylightDetector(AbstractBlock.Settings settings) {
        return new NewDaylightDetectorBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable());
    }

    /*@Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;SNOW:Lnet/minecraft/block/Block;")))
    private static Block ice(String id, Block block) {
        return register("ice", new NewIceBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_PURPLE).slipperiness(0.98F).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning( (state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR).solidBlock(Blocks::never)));
    }*/
    /*@ModifyArg(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At(value = "FIELD",
                  target = "Lnet/minecraft/block/Blocks;SNOW:Lnet/minecraft/block/Block;")), index = 1)
    private static Block ice(String id, Block block) {
        return new NewIceBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_PURPLE).slipperiness(0.98F).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning( (state, world, pos, entityType) -> entityType == EntityType.POLAR_BEAR).solidBlock(Blocks::never));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;COAL_BLOCK:Lnet/minecraft/block/Block;")))
    private static Block packedIce(String id, Block block) {
        return register("packed_ice", new PackedIceBlock(AbstractBlock.Settings.create().ticksRandomly().mapColor(MapColor.PALE_PURPLE).instrument(NoteBlockInstrument.CHIME).slipperiness(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;SEA_PICKLE:Lnet/minecraft/block/Block;")))
    private static Block blueIce(String id, Block block) {
        return register("blue_ice", new BlueIceBlock(AbstractBlock.Settings.create().ticksRandomly().mapColor(MapColor.PALE_PURPLE).strength(2.8F).slipperiness(0.989F).sounds(BlockSoundGroup.GLASS)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;BLACK_CANDLE_CAKE:Lnet/minecraft/block/Block;")))
    private static Block powerAmethystBlock(String id, Block block) {
        return register("amethyst_block", new NewAmethystBlock(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).strength(1.5F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool()));
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
                        .mapColor(MapColor.DARK_GREEN)
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
                        .mapColor(MapColor.DARK_GREEN)
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
                        .mapColor(MapColor.DARK_GREEN)
                        .noCollision()
                        .breakInstantly()
                        .sounds(BlockSoundGroup.CROP)
                        .offset(AbstractBlock.OffsetType.XZ)
                        .burnable()
                        .pistonBehavior(PistonBehavior.DESTROY)
        );
    }

    @Unique
    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, id, block);
    }
*/
}
