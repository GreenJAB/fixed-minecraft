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
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.IceBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

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

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=torchflower"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/registry/entry/RegistryEntry;FLnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/FlowerBlock;", ordinal = 0))
    private static FlowerBlock newTorchFlower(RegistryEntry stewEffect, float effectLengthInSeconds, AbstractBlock.Settings settings) {
        return new NewTorchFlowerBlock(StatusEffects.NIGHT_VISION, 5.0F,
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

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=pitcher_crop"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/PitcherCropBlock;", ordinal = 0))
    private static PitcherCropBlock newPitcherCrop(AbstractBlock.Settings settings) {
        return new NewPitcherCropBlock(
                AbstractBlock.Settings.create()
                        .mapColor(MapColor.DARK_GREEN)
                        .noCollision()
                        .ticksRandomly()
                        .breakInstantly()
                        .sounds(BlockSoundGroup.CROP)
                        .pistonBehavior(PistonBehavior.DESTROY));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=pitcher_plant"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/TallPlantBlock;", ordinal = 0))
    private static TallPlantBlock newPitcherPod(AbstractBlock.Settings settings) {
        return new NewPitcherPlantBlock(
                AbstractBlock.Settings.create()
                        .mapColor(MapColor.DARK_GREEN)
                        .noCollision()
                        .breakInstantly()
                        .sounds(BlockSoundGroup.CROP)
                        .offset(AbstractBlock.OffsetType.XZ)
                        .burnable()
                        .pistonBehavior(PistonBehavior.DESTROY));
    }


    @Unique
    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, id, block);
    }
}
