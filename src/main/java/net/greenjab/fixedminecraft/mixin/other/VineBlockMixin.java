package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin {

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;", ordinal = 0))
    private void growFromCeiling(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci,
                                 @Local Direction testDirection) {
        VineBlock VN = (VineBlock)(Object)this;
        if (!testDirection.getAxis().isHorizontal()) return;
        if (state.getValue(VineBlock.getPropertyForFace(Direction.UP))){
            int i =0;
            int k = 0;

            i+= state.getValue(VineBlock.getPropertyForFace(Direction.NORTH)) ?1:0;
            i+= state.getValue(VineBlock.getPropertyForFace(Direction.SOUTH)) ?1:0;
            i+= state.getValue(VineBlock.getPropertyForFace(Direction.EAST)) ?1:0;
            i+= state.getValue(VineBlock.getPropertyForFace(Direction.WEST)) ?1:0;

            i+=(level.getBlockState(pos.north()).is(Blocks.VINE))?1:0;
            i+=(level.getBlockState(pos.south()).is(Blocks.VINE))?1:0;
            i+=(level.getBlockState(pos.east()).is(Blocks.VINE))?1:0;
            i+=(level.getBlockState(pos.west()).is(Blocks.VINE))?1:0;

            k+=(level.getBlockState(pos.north()).isFaceSturdy(level, pos.north(), Direction.SOUTH, SupportType.FULL))?1:0;
            k+=(level.getBlockState(pos.south()).isFaceSturdy(level, pos.south(), Direction.NORTH, SupportType.FULL))?1:0;
            k+=(level.getBlockState(pos.east()).isFaceSturdy(level, pos.east(), Direction.WEST, SupportType.FULL))?1:0;
            k+=(level.getBlockState(pos.west()).isFaceSturdy(level, pos.west(), Direction.EAST, SupportType.FULL))?1:0;

            if (i == 0) {
                if (k != 0) {
                    if (level.getBlockState(pos.relative(testDirection)).isFaceSturdy(level, pos.relative(testDirection), testDirection.getOpposite(), SupportType.FULL))
                        level.setBlock(pos, VN.defaultBlockState()
                                .setValue(VineBlock.getPropertyForFace(testDirection), true)
                                .setValue(VineBlock.getPropertyForFace(Direction.UP), true), Block.UPDATE_CLIENTS);
                }
                else {
                    level.setBlock(pos, VN.defaultBlockState()
                            .setValue(VineBlock.getPropertyForFace(testDirection), true)
                            .setValue(VineBlock.getPropertyForFace(Direction.UP), true), Block.UPDATE_CLIENTS);
                }

            }

            //if (i==0&&j==4) world.setBlockState(pos, VN.getDefaultState().with(VN.getFacingProperty(direction), true).with(VN.getFacingProperty(Direction.UP),true), Block.NOTIFY_LISTENERS);
        }
    }
}
