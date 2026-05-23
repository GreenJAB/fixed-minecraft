package net.greenjab.fixedminecraft.mixin.map_book;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapItem.class)
public abstract class MapItemMixin {

    @Unique
    boolean tinted = false;

    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasCeiling()Z"))
    private boolean updateNetherColours(boolean original) {
        tinted = false;
        return false;
    }


    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState tryUseDarkestValue(BlockState original) {
        if (original.is(Blocks.TINTED_GLASS)) tinted = true;
        return original;
    }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/MapColor;getPackedId(Lnet/minecraft/world/level/material/MapColor$Brightness;)B"))
    private MapColor.Brightness tryUseDarkestValue(MapColor.Brightness brightness) {
        if (tinted) return MapColor.Brightness.LOWEST;
        return brightness;
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getMapColor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/MapColor;", ordinal = 3))
    private MapColor biomeColours(BlockState instance, BlockGetter blockView, BlockPos blockPos, @Local(argsOnly = true) Level level) {
        if (instance.is(Blocks.GRASS_BLOCK)){
            if (level.getBiome(blockPos).is(BiomeTags.IS_SAVANNA)) {
                return MapColor.TERRACOTTA_YELLOW;
            }
            if (level.getBiome(blockPos).is(BiomeTags.HAS_SWAMP_HUT)) {
                return MapColor.PLANT;
            }
        }
        if (instance.is(Blocks.OAK_LEAVES) || instance.is(Blocks.VINE)){
            if (level.getBiome(blockPos).is(BiomeTags.HAS_SWAMP_HUT)) {
                return MapColor.TERRACOTTA_GREEN;
            }
        }
        return instance.getMapColor(blockView, blockPos);
    }


}
