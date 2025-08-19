package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.block.InteractibleSlotContainer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(InteractibleSlotContainer.class)
public interface ChiseledBookshelfBlockInvoker {
    @Invoker("getHitPosOnFront")
    static Optional<Vec2f> getHitPosOnFront(BlockHitResult hit, Direction facing) {
        throw new AssertionError();
    }

}
