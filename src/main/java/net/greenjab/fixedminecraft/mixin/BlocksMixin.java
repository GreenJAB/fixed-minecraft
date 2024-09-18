package net.greenjab.fixedminecraft.mixin;

import net.greenjab.fixedminecraft.registry.block.BlueIceBlock;
import net.greenjab.fixedminecraft.registry.block.NewDaylightDetectorBlock;
import net.greenjab.fixedminecraft.registry.block.NewIceBlock;
import net.greenjab.fixedminecraft.registry.block.PackedIceBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.IceBlock;
import net.minecraft.block.TranslucentBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public class BlocksMixin {


    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args= {
            "stringValue=ice"}, ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/IceBlock;", ordinal = 0 ))
    private static IceBlock Ice(AbstractBlock.Settings settings) {
        return new NewIceBlock(settings);
    }
    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=packed_ice"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", ordinal = 0 ))
    private static Block packedIce(AbstractBlock.Settings settings) {
        return new PackedIceBlock(settings.ticksRandomly());
    }
    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=blue_ice"},ordinal = 0)),at = @At(
                    value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/TranslucentBlock;", ordinal = 0 ))
    private static TranslucentBlock blueIce(AbstractBlock.Settings settings) {
        return new BlueIceBlock(settings.ticksRandomly());
    }
    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=daylight_detector"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/DaylightDetectorBlock;", ordinal = 0 ))
    private static DaylightDetectorBlock compOutputDaylightDetector(AbstractBlock.Settings settings) {
        return new NewDaylightDetectorBlock(settings);
    }

}
