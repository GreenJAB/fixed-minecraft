package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin {
    @Redirect(method = "useWithoutItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;cycle(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Object;"
    ))
    private Object goDownNote(BlockState instance, Property<Integer> property,
                              @Local(argsOnly = true) BlockHitResult hitResult, @Local(argsOnly = true) Player player) {
        if (player.hasInfiniteMaterials() && player.isShiftKeyDown()) return instance;
        if (isTopHalf(hitResult)) return instance.cycle(property);
        else return instance.setValue(property, getPrev(property.getPossibleValues(), instance.getValue(property)));
    }
    @Unique
    public final boolean isTopHalf(BlockHitResult hit) {
        Direction direction = hit.getDirection();
        if (direction == Direction.UP) return true;
        if (direction == Direction.DOWN) return false;
        BlockPos blockPos = hit.getBlockPos().relative(direction);
        Vec3 vec3d = hit.getLocation().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return vec3d.y() >= 0.5F;
    }

    @Unique
    private static <T> T getPrev(List<T> values, T value) {
        int i = values.indexOf(value) - 1;
        return (T)(i == -1 ? values.size()-1 : values.get(i));
    }
}
