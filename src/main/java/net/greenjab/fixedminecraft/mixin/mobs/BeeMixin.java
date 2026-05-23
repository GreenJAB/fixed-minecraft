package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bee.class)
public abstract class BeeMixin {

    @Inject(method = "setHasNectar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/bee/Bee;resetTicksWithoutNectarSinceExitingHive()V"))
    private void spreadFlowers(boolean hasNectar, CallbackInfo ci){
        Bee bee = (Bee)(Object)this;
        Level world = bee.level();
        BlockPos pos = bee.getSavedFlowerPos();
        if (pos != null) {
            BlockState flower = world.getBlockState(pos);
            if (pos.getY() > -500 && (flower.is(BlockTags.SMALL_FLOWERS) || (flower.is(BlockTags.FLOWERS) && flower.getProperties().contains(DoublePlantBlock.HALF)))) {
                boolean tall = false;
                if (flower.is(BlockTags.FLOWERS) && flower.getProperties().contains(DoublePlantBlock.HALF)) {
                    tall = true;
                    BlockState below = world.getBlockState(pos.below());
                    if (flower.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER && (below.is(BlockTags.FLOWERS) && below.getProperties().contains(DoublePlantBlock.HALF))) {
                        pos = pos.below();
                    }
                }
                int count = 0;
                for (int x = -4; x <= 4; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -4; z <= 4; z++) {
                            if (world.getBlockState(pos.offset(x, y, z)) == flower) count++;
                        }
                    }
                }
                if (count < 5) {
                    int i = 0;
                    while (i < 10) {
                        int x = world.getRandom().nextInt(5) - 2;
                        int y = world.getRandom().nextInt(3) - 1;
                        int z = world.getRandom().nextInt(5) - 2;
                        BlockPos newFlower = pos.offset(x, y, z);
                        if (world.getBlockState(newFlower) == Blocks.AIR.defaultBlockState()) {
                            if (world.getBlockState(newFlower.below()).is(BlockTags.DIRT)) {
                                if (!tall) {
                                    world.setBlockAndUpdate(newFlower, flower);
                                    i = 10;
                                } else {
                                    if (world.getBlockState(newFlower.above()) == Blocks.AIR.defaultBlockState()) {
                                        world.setBlockAndUpdate(newFlower, flower.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
                                        world.setBlockAndUpdate(newFlower.above(), flower.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
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
        bee.setSavedFlowerPos(new BlockPos(0, -1000, 0));
    }
}
