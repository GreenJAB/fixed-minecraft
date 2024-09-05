package net.greenjab.fixedminecraft.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.VineBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VineBlock.class)
public class VineMixin {

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"))
    private void injected(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci,
                          @Local Direction direction) {
        VineBlock VN = (VineBlock)(Object)this;
        if (!direction.getAxis().isHorizontal()) return;
        if (state.get(VN.getFacingProperty(Direction.UP))){
            int i =0;
            int k = 0;

            i+= state.get(VN.getFacingProperty(Direction.NORTH)) ?1:0;
            i+= state.get(VN.getFacingProperty(Direction.SOUTH)) ?1:0;
            i+= state.get(VN.getFacingProperty(Direction.EAST)) ?1:0;
            i+= state.get(VN.getFacingProperty(Direction.WEST)) ?1:0;

            i+=(world.getBlockState(pos.north()).isOf(Blocks.VINE))?1:0;
            i+=(world.getBlockState(pos.south()).isOf(Blocks.VINE))?1:0;
            i+=(world.getBlockState(pos.east()).isOf(Blocks.VINE))?1:0;
            i+=(world.getBlockState(pos.west()).isOf(Blocks.VINE))?1:0;

            k+=(world.getBlockState(pos.north()).isSideSolid(world, pos.north(), Direction.SOUTH, SideShapeType.FULL))?1:0;
            k+=(world.getBlockState(pos.south()).isSideSolid(world, pos.south(), Direction.NORTH, SideShapeType.FULL))?1:0;
            k+=(world.getBlockState(pos.east()).isSideSolid(world, pos.east(), Direction.WEST, SideShapeType.FULL))?1:0;
            k+=(world.getBlockState(pos.west()).isSideSolid(world, pos.west(), Direction.EAST, SideShapeType.FULL))?1:0;

            if (i == 0) {
                if (k != 0) {
                    if (world.getBlockState(pos.offset(direction)).isSideSolid(world, pos.offset(direction), direction.getOpposite(), SideShapeType.FULL))
                        world.setBlockState(pos, VN.getDefaultState()
                                .with(VN.getFacingProperty(direction), true)
                                .with(VN.getFacingProperty(Direction.UP), true), Block.NOTIFY_LISTENERS);
                }
                else {
                    world.setBlockState(pos, VN.getDefaultState()
                            .with(VN.getFacingProperty(direction), true)
                            .with(VN.getFacingProperty(Direction.UP), true), Block.NOTIFY_LISTENERS);
                }

            }

            //if (i==0&&j==4) world.setBlockState(pos, VN.getDefaultState().with(VN.getFacingProperty(direction), true).with(VN.getFacingProperty(Direction.UP),true), Block.NOTIFY_LISTENERS);
        }
    }
}
