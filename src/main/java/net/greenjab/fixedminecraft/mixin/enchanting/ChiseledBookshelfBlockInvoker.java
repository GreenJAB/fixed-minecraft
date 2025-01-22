package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import java.util.OptionalInt;

@Mixin(ChiseledBookshelfBlock.class)
public interface ChiseledBookshelfBlockInvoker {
    @Invoker("getHitPos")
    static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
        throw new AssertionError();
    }

    @Invoker("getSlotForHitPos")
    static OptionalInt getSlotForHitPos(BlockHitResult hit, BlockState state) {
        throw new AssertionError();
    }
}
