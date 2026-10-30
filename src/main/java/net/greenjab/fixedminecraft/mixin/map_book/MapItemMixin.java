package net.greenjab.fixedminecraft.mixin.map_book;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(MapItem.class)
public abstract class MapItemMixin {

    @Unique int tint = -1;
    @Unique boolean darker = false;

    @ModifyExpressionValue(method = "update", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasCeiling()Z"))
    private boolean updateTintedColours(boolean original) {
        tint = -1;
        darker = false;
        return false;
    }

    @Unique Map<Block, Integer> bulbMap =Map.of(
            Blocks.WAXED_COPPER_BULB, 3,
            Blocks.WAXED_EXPOSED_COPPER_BULB, 2,
            Blocks.WAXED_WEATHERED_COPPER_BULB, 1,
            Blocks.WAXED_OXIDIZED_COPPER_BULB, 0,
            Blocks.COPPER_BULB, 3,
            Blocks.EXPOSED_COPPER_BULB, 2,
            Blocks.WEATHERED_COPPER_BULB, 1,
            Blocks.OXIDIZED_COPPER_BULB, 0
    );

    @Unique Map<Integer, Integer> darkerMap =Map.of(
            0, 3,
            1, 0,
            2, 1,
            3, 2
    );

    @ModifyExpressionValue(method = "update", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState setupTint(BlockState original) {
        if (original.is(Blocks.TINTED_GLASS)) tint = 0;
        if (original.getBlock() instanceof CopperBulbBlock) {
            if (original.getValue(CopperBulbBlock.LIT)) {
                tint =  bulbMap.get(original.getBlock());
            }
        }
        return original;
    }

    @ModifyArg(method = "update", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/MapColor;getPackedId(Lnet/minecraft/world/level/material/MapColor$Brightness;)B"))
    private MapColor.Brightness overrideTint(MapColor.Brightness brightness) {
        if (tint==0) return MapColor.Brightness.LOWEST;
        if (tint==1) return MapColor.Brightness.LOW;
        if (tint==2) return MapColor.Brightness.NORMAL;
        if (tint==3) return MapColor.Brightness.HIGH;
        if (darker) return MapColor.Brightness.byId(darkerMap.get(brightness.id));
        return brightness;
    }

    @WrapOperation(method = "update", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getMapColor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/MapColor;", ordinal = 3))
    private MapColor biomeColours(BlockState instance, BlockGetter blockGetter, BlockPos pos, Operation<MapColor> original,
                                  @Local(argsOnly = true) Level level) {
        Holder<Biome> biome = level.getBiome(pos);
        if (instance.is(Blocks.GRASS_BLOCK)){
            if (biome.is(BiomeTags.IS_SAVANNA))  return MapColor.TERRACOTTA_YELLOW;
            if (biome.is(BiomeTags.HAS_SWAMP_HUT)||biome.is(BiomeTags.IS_BADLANDS)) return MapColor.TERRACOTTA_LIGHT_GREEN;
            if (biome.is(Biomes.PALE_GARDEN)) return MapColor.DEEPSLATE;
            if (biome.is(BiomeTags.IS_JUNGLE)||biome.is(Biomes.MUSHROOM_FIELDS)) return MapColor.COLOR_LIGHT_GREEN;
        } else if (instance.is(Blocks.MYCELIUM) ){
            return MapColor.TERRACOTTA_PURPLE;
        } else if (instance.is(Blocks.OAK_LEAVES) || instance.is(Blocks.VINE) || instance.is(Blocks.ACACIA_LEAVES)) {
            if (biome.is(BiomeTags.HAS_SWAMP_HUT)||biome.is(BiomeTags.IS_BADLANDS)) return MapColor.TERRACOTTA_GREEN;
            if (biome.is(BiomeTags.IS_SAVANNA)) return MapColor.TERRACOTTA_LIGHT_GREEN;
        } else if (instance.getBlock() instanceof CopperBulbBlock){
            if (instance.getValue(CopperBulbBlock.LIT)) return MapColor.COLOR_YELLOW;
        } else if (instance.is(Blocks.WATER)){
            if (biome.is(Biomes.FROZEN_OCEAN) || biome.is(Biomes.DEEP_FROZEN_OCEAN)
                || biome.is(Biomes.COLD_OCEAN) || biome.is(Biomes.DEEP_COLD_OCEAN)) darker = true;
            if (biome.is(Biomes.WARM_OCEAN)) return MapColor.DIAMOND;
            if (biome.is(Biomes.LUKEWARM_OCEAN) || biome.is(Biomes.DEEP_LUKEWARM_OCEAN) || biome.is(Biomes.CHERRY_GROVE)) return MapColor.LAPIS;
            if (biome.is(Biomes.FROZEN_OCEAN) || biome.is(Biomes.DEEP_FROZEN_OCEAN)) return MapColor.COLOR_BLUE;
            if (biome.is(BiomeTags.HAS_SWAMP_HUT) || biome.is(Biomes.MANGROVE_SWAMP)) return MapColor.TERRACOTTA_CYAN;
        }

        return original.call(instance, blockGetter, pos);
    }

    @ModifyExpressionValue(method = "update", at =
    @At(value = "FIELD", target = "Lnet/minecraft/world/level/material/MapColor;WATER:Lnet/minecraft/world/level/material/MapColor;", opcode = Opcodes.GETSTATIC))
    private MapColor allowOtherWaterColours(MapColor original, @Local(ordinal = 11) int waterDepth, @Local(ordinal = 0) MapColor color){
        if (waterDepth > 0) return color;
        return original;
    }
}
