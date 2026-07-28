package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin {

    @WrapOperation(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean powerDoubleDoor(Level level, BlockPos pos, Operation<Boolean> original, @Local(argsOnly = true) BlockState state){
        boolean basePower = original.call(level, pos);
        BlockPos otherPos = getOtherDoor(level, pos, state);
        boolean otherPower = otherPos != null && original.call(level, otherPos);
        return basePower || otherPower;
    }

    @WrapOperation(method = {"setOpen","useWithoutItem","neighborChanged"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean openDoubleDoor(Level level, BlockPos pos, BlockState blockState, int updateFlags, Operation<Boolean> original){
        boolean result = original.call(level, pos, blockState, updateFlags);
        BlockPos otherPos = getOtherDoor(level, pos, blockState);
        if (otherPos != null) original.call(level, otherPos, blockState.setValue(DoorBlock.HINGE,
                blockState.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT), updateFlags);
        return result;
    }

    @Unique
    private BlockPos getOtherDoor(Level level, BlockPos pos, BlockState state) {
        Direction dir = state.getValue(DoorBlock.FACING);
        dir = state.getValue(DoorBlock.HINGE)== DoorHingeSide.LEFT?dir.getClockWise():dir.getCounterClockWise();
        BlockState otherState = level.getBlockState(pos.relative(dir));
        if (otherState.is(state.getBlock()) && otherState.getValue(DoorBlock.FACING) == state.getValue(DoorBlock.FACING)
                && otherState.getValue(DoorBlock.HALF) == state.getValue(DoorBlock.HALF)
                && otherState.getValue(DoorBlock.HINGE) != state.getValue(DoorBlock.HINGE)) {
            return pos.relative(dir);
        }
        return null;
    }
}
