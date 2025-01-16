package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeeEntity.class)
public abstract class BeeEntityMixin {

    @Redirect(method = "setHasNectar", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BeeEntity;resetPollinationTicks()V"))
    private void spreadFlowers(BeeEntity instance){
        World world = instance.getWorld();
        BlockPos pos = instance.getFlowerPos();
        if (pos != null) {
            BlockState flower = world.getBlockState(pos);
            if (pos.getY() > -500 && flower.isIn(BlockTags.FLOWERS)) {
                boolean tall = false;
                if (!flower.isIn(BlockTags.SMALL_FLOWERS)) {
                    tall = true;
                    if (flower.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                        pos = pos.down();
                    }
                }
                int count = 0;
                for (int x = -4; x <= 4; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -4; z <= 4; z++) {
                            if (world.getBlockState(pos.add(x, y, z)) == flower) count++;
                        }
                    }
                }
                if (count < 5) {
                    int i = 0;
                    while (i < 10) {
                        int x = world.random.nextInt(5) - 2;
                        int y = world.random.nextInt(3) - 1;
                        int z = world.random.nextInt(5) - 2;
                        BlockPos newFlower = pos.add(x, y, z);
                        if (world.getBlockState(newFlower) == Blocks.AIR.getDefaultState()) {
                            if (world.getBlockState(newFlower.down()).isIn(BlockTags.DIRT)) {
                                if (!tall) {
                                    world.setBlockState(newFlower, flower);
                                    i = 10;
                                } else {
                                    if (world.getBlockState(newFlower.up()) == Blocks.AIR.getDefaultState()) {
                                        world.setBlockState(newFlower, flower.with(TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
                                        world.setBlockState(newFlower.up(), flower.with(TallPlantBlock.HALF, DoubleBlockHalf.UPPER));
                                        i = 10;
                                    }
                                }
                            }
                        i++;
                        }
                    }
                }
            }
        }
        instance.setFlowerPos(new BlockPos(0, -1000, 0));
    }
}
