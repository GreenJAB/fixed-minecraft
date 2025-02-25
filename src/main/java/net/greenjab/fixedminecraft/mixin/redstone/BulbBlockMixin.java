package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BulbBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Because yes
 */
@Mixin(BulbBlock.class)
public abstract class BulbBlockMixin extends Block {
    public BulbBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public abstract void update(BlockState state, ServerWorld world, BlockPos pos);

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        update(state, world, pos);
    }

    @WrapOperation(
            method = {"onBlockAdded", "neighborUpdate"}, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BulbBlock;update(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V"
    )
    )
    private void undoMojankCringe(BulbBlock instance, BlockState state, ServerWorld world, BlockPos pos, Operation<Void> original) {
        int delay = 0;
        if (state.toString().toLowerCase().contains("exposed")) delay = 1;
        if (state.toString().toLowerCase().contains("weathered")) delay = 2;
        if (state.toString().toLowerCase().contains("oxidized")) delay = 3;
        if (delay > 0) {
            world.scheduleBlockTick(pos, instance, delay);
        } else {
            this.update(state, world, pos);
        }
    }
}
