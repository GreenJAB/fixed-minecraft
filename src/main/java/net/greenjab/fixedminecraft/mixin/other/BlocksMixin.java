package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.block.NewIceBlock;
import net.greenjab.fixedminecraft.registry.block.PackedIceBlock;
import net.greenjab.fixedminecraft.registry.block.BlueIceBlock;
import net.greenjab.fixedminecraft.registry.block.NewAmethystBlock;
import net.greenjab.fixedminecraft.registry.block.NewDaylightDetectorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

@Mixin(Blocks.class)
public class BlocksMixin {

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

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice( from = @At( value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;COMPARATOR:Lnet/minecraft/block/Block;")))
    private static Block compOutputDaylightDetector(String id, Block block) {
        return register("daylight_detector", new NewDaylightDetectorBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(0.2F).sounds(BlockSoundGroup.WOOD).burnable()));
    }

    @Unique
    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, id, block);
    }
*/
}
