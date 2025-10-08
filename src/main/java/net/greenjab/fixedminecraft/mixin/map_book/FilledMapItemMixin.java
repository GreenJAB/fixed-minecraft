package net.greenjab.fixedminecraft.mixin.map_book;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.FilledMapItem;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FilledMapItem.class)
public class FilledMapItemMixin  {

    @Unique
    boolean tinted = false;

    @ModifyExpressionValue(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/DimensionType;hasCeiling()Z"))
    private boolean updateNetherColours(boolean original) {
        tinted = false;
        return false;
    }


    @ModifyExpressionValue(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState tryUseDarkestValue(BlockState original) {
        if (original.isOf(Blocks.TINTED_GLASS)) tinted = true;
        return original;
    }

    @ModifyArg(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/MapColor;getRenderColorByte(Lnet/minecraft/block/MapColor$Brightness;)B"))
    private MapColor.Brightness tryUseDarkestValue(MapColor.Brightness brightness) {
        if (tinted) return MapColor.Brightness.LOWEST;
        return brightness;
    }

    @Redirect(method = "updateColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getMapColor(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/MapColor;", ordinal = 3))
    private MapColor biomeColours(BlockState instance, BlockView blockView, BlockPos blockPos, @Local(argsOnly = true) World world) {
        if (instance.isOf(Blocks.GRASS_BLOCK)){
            if (world.getBiome(blockPos).isIn(BiomeTags.IS_SAVANNA)) {
                return MapColor.TERRACOTTA_YELLOW;
            }
            if (world.getBiome(blockPos).isIn(BiomeTags.SWAMP_HUT_HAS_STRUCTURE)) {
                return MapColor.DARK_GREEN;
            }
        }
        if (instance.isOf(Blocks.OAK_LEAVES) || instance.isOf(Blocks.VINE)){
            if (world.getBiome(blockPos).isIn(BiomeTags.SWAMP_HUT_HAS_STRUCTURE)) {
                return MapColor.TERRACOTTA_GREEN;
            }
        }
        return instance.getMapColor(blockView, blockPos);
    }


}
