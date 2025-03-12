package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin {
    @Redirect(method = "onUse", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;cycle(Lnet/minecraft/state/property/Property;)Ljava/lang/Object;"
    ))
    private Object goDownNote(BlockState instance, Property<Integer> property,
                              @Local(argsOnly = true) BlockHitResult hit, @Local(argsOnly = true) PlayerEntity player) {
        if (player.getAbilities().creativeMode && player.isSneaking()) return instance;
        if (isTopHalf(hit)) return instance.cycle(property);
        else return instance.with(property, getPrev(property.getValues(), instance.get(property)));
    }
    @Unique
    public final boolean isTopHalf(BlockHitResult hit) {
        Direction direction = hit.getSide();
        if (direction == Direction.UP) return true;
        if (direction == Direction.DOWN) return false;
        BlockPos blockPos = hit.getBlockPos().offset(direction);
        Vec3d vec3d = hit.getPos().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return vec3d.getY() >= 0.5F;
    }

    @Unique
    private static <T> T getPrev(List<T> values, T value) {
        int i = values.indexOf(value) - 1;
        return (T)(i == -1 ? values.size()-1 : values.get(i));
    }
}
