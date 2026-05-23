package net.greenjab.fixedminecraft.mixin.enchanting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SelectableSlotContainer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;

@Mixin(SelectableSlotContainer.class)
public interface ChiseledBookshelfBlockInvoker {
    @Invoker("getRelativeHitCoordinatesForBlockFace")
    static Optional<Vec2> getHitPosOnFront(BlockHitResult hit, Direction facing) {
        throw new AssertionError();
    }

}
