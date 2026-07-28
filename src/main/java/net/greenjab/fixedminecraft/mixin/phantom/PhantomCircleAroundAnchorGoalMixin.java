package net.greenjab.fixedminecraft.mixin.phantom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.monster.Phantom$PhantomCircleAroundAnchorGoal")
public abstract class PhantomCircleAroundAnchorGoalMixin {

    @Shadow @Final Phantom this$0;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z", ordinal = 0))
    private boolean dive(Level instance, BlockPos pos, Operation<Boolean> original){
        Phantom PE = this.this$0;
        if (PE.noPhysics) {
            Level world = PE.level();
            BlockPos blockpos = PE.blockPosition();
            ChunkPos chunk = world.getChunkAt(blockpos).getPos();
            BlockGetter blockView = world.getChunkForCollisions(chunk.x(), chunk.z());
            if (PE.level().getBlockState(pos.above(4)).isRedstoneConductor(blockView, pos)) PE.discard();
            return true;
        } else return original.call(instance, pos);
    }
}
