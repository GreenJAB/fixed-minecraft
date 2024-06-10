package net.greenjab.fixedminecraft.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
            int j = 0;

            i+= state.get(VN.getFacingProperty(Direction.NORTH)) ?1:0;
            i+= state.get(VN.getFacingProperty(Direction.SOUTH)) ?1:0;
            i+= state.get(VN.getFacingProperty(Direction.EAST)) ?1:0;
            i+= state.get(VN.getFacingProperty(Direction.WEST)) ?1:0;

            j+=(world.getBlockState(pos.north())==Blocks.AIR.getDefaultState())?1:0;
            j+=(world.getBlockState(pos.south())==Blocks.AIR.getDefaultState())?1:0;
            j+=(world.getBlockState(pos.east())==Blocks.AIR.getDefaultState())?1:0;
            j+=(world.getBlockState(pos.west())==Blocks.AIR.getDefaultState())?1:0;

            if (i==0&&j==4) world.setBlockState(pos, VN.getDefaultState().with(VN.getFacingProperty(direction), true).with(VN.getFacingProperty(Direction.UP),true), Block.NOTIFY_LISTENERS);
        }
    }
}
